package com.ucc.convenios.notifications.service;

import com.ucc.convenios.shared.exceptions.BadRequestException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        String normalizedTo = normalizeEmail(to);

        if (normalizedTo.isBlank()) {
            LOGGER.warn("No se envió correo porque el destinatario está vacío. Asunto: {}", subject);
            return;
        }

        if (!mailEnabled) {
            LOGGER.info("[MAIL DISABLED] To: {}", normalizedTo);
            LOGGER.info("[MAIL DISABLED] Subject: {}", subject);
            LOGGER.debug("[MAIL DISABLED] Body: {}", htmlBody);
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(normalizedTo);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            javaMailSender.send(message);
            LOGGER.info("Correo enviado correctamente a {} con asunto '{}'", normalizedTo, subject);
        } catch (Exception exception) {
            LOGGER.error("No se pudo enviar el correo a {} con asunto '{}'", normalizedTo, subject, exception);
            throw new BadRequestException("No se pudo enviar el correo. Detalle: " + exception.getMessage());
        }
    }

    public boolean isMailEnabled() {
        return mailEnabled;
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }

        return email.trim().toLowerCase();
    }
}