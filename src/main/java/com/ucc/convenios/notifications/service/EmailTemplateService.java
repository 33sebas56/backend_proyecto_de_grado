package com.ucc.convenios.notifications.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    public String buildRegisterCodeSubject() {
        return "Código de verificación - Sistema de Convenios UCC";
    }

    public String buildRegisterCodeHtml(String code, int expirationMinutes) {
        return """
                <html>
                    <body style="font-family: Arial, sans-serif; color: #222;">
                        <h2>Sistema de Gestión de Convenios UCC</h2>
                        <p>Recibimos una solicitud para crear una cuenta en el sistema.</p>
                        <p>Tu código de verificación es:</p>
                        <div style="font-size: 28px; font-weight: bold; letter-spacing: 4px; margin: 18px 0;">
                            %s
                        </div>
                        <p>Este código vence en <strong>%d minutos</strong>.</p>
                        <p>Si no solicitaste este código, puedes ignorar este mensaje.</p>
                        <hr>
                        <p style="font-size: 12px; color: #666;">
                            Este correo fue generado automáticamente. No respondas a este mensaje.
                        </p>
                    </body>
                </html>
                """.formatted(code, expirationMinutes);
    }
}