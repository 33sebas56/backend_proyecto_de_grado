package com.ucc.convenios.auth.service;

import com.ucc.convenios.auth.dto.RequestRegisterCodeRequest;
import com.ucc.convenios.auth.entity.EmailVerificationCode;
import com.ucc.convenios.auth.repository.EmailVerificationCodeRepository;
import com.ucc.convenios.notifications.service.EmailTemplateService;
import com.ucc.convenios.notifications.service.MailService;
import com.ucc.convenios.shared.enums.EmailCodePurpose;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class EmailVerificationCodeService {

    private static final int MAX_ATTEMPTS = 5;

    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.mail.register-code-expiration-minutes:10}")
    private int registerCodeExpirationMinutes;

    public EmailVerificationCodeService(
            EmailVerificationCodeRepository emailVerificationCodeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            EmailTemplateService emailTemplateService
    ) {
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.emailTemplateService = emailTemplateService;
    }

    @Transactional
    public void requestRegisterCode(RequestRegisterCodeRequest request) {
        String email = normalizeEmail(request.getEmail());

        validateInstitutionalEmail(email);

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Ya existe un usuario registrado con este correo");
        }

        String code = generateSixDigitCode();

        EmailVerificationCode verificationCode = new EmailVerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setPurpose(EmailCodePurpose.REGISTER);
        verificationCode.setCodeHash(passwordEncoder.encode(code));
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(registerCodeExpirationMinutes));
        verificationCode.setAttempts(0);

        emailVerificationCodeRepository.save(verificationCode);

        String subject = emailTemplateService.buildRegisterCodeSubject();
        String htmlBody = emailTemplateService.buildRegisterCodeHtml(code, registerCodeExpirationMinutes);

        mailService.sendHtmlEmail(email, subject, htmlBody);
    }

    @Transactional
    public void verifyRegisterCode(String rawEmail, String code) {
        String email = normalizeEmail(rawEmail);

        EmailVerificationCode verificationCode = emailVerificationCodeRepository
                .findFirstByEmailAndPurposeAndUsedAtIsNullOrderByCreatedAtDesc(email, EmailCodePurpose.REGISTER)
                .orElseThrow(() -> new BadRequestException("No hay un código activo para este correo"));

        if (verificationCode.getAttempts() >= MAX_ATTEMPTS) {
            throw new BadRequestException("El código superó el máximo de intentos permitidos");
        }

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código de verificación ya venció");
        }

        boolean validCode = passwordEncoder.matches(code, verificationCode.getCodeHash());

        if (!validCode) {
            verificationCode.setAttempts(verificationCode.getAttempts() + 1);
            emailVerificationCodeRepository.save(verificationCode);
            throw new BadRequestException("Código de verificación inválido");
        }

        verificationCode.setUsedAt(LocalDateTime.now());
        emailVerificationCodeRepository.save(verificationCode);
    }

    private String generateSixDigitCode() {
        int number = secureRandom.nextInt(1_000_000);
        return String.format("%06d", number);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }

        return email.trim().toLowerCase();
    }

    private void validateInstitutionalEmail(String email) {
        if (!email.endsWith("@campusucc.edu.co")) {
            throw new BadRequestException("Solo se permiten correos institucionales @campusucc.edu.co");
        }
    }
}