package com.ucc.convenios.notifications.service;

import com.ucc.convenios.shared.exceptions.BadRequestException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        if (!mailEnabled) {
            System.out.println("[MAIL DISABLED] To: " + to);
            System.out.println("[MAIL DISABLED] Subject: " + subject);
            System.out.println("[MAIL DISABLED] Body: " + htmlBody);
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            javaMailSender.send(message);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BadRequestException("No se pudo enviar el correo. Detalle: " + exception.getMessage());
        }
    }
}