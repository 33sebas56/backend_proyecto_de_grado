package com.ucc.convenios.notifications.service;

import com.ucc.convenios.shared.enums.ConvenioStage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailTemplateService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String buildRegisterCodeSubject() {
        return "Código de verificación - Sistema de Convenios UCC";
    }

    public String buildRegisterCodeHtml(String code, int expirationMinutes) {
        return wrapHtml("""
                <h2>Sistema de Gestión de Convenios UCC</h2>
                <p>Recibimos una solicitud para crear una cuenta en el sistema.</p>
                <p>Tu código de verificación es:</p>
                <div style="font-size: 28px; font-weight: bold; letter-spacing: 4px; margin: 18px 0;">
                    %s
                </div>
                <p>Este código vence en <strong>%d minutos</strong>.</p>
                <p>Si no solicitaste este código, puedes ignorar este mensaje.</p>
                """.formatted(escapeHtml(code), expirationMinutes));
    }

    public String buildWelcomeSubject() {
        return "Bienvenido al Sistema de Convenios UCC";
    }

    public String buildWelcomeHtml(String fullName, String systemUrl) {
        return wrapHtml("""
                <h2>Registro completado</h2>
                <p>Hola <strong>%s</strong>.</p>
                <p>Tu cuenta fue creada correctamente en el Sistema de Gestión de Convenios UCC.</p>
                <p>Inicialmente tu usuario queda con el rol base <strong>SOLICITANTE</strong>.</p>
                <p>Un administrador podrá asignarte los roles correspondientes según tus funciones dentro del flujo.</p>
                <p>Puedes ingresar al sistema desde el siguiente enlace:</p>
                <p><a href="%s">Abrir sistema</a></p>
                """.formatted(
                escapeHtml(fullName),
                escapeHtml(systemUrl)
        ));
    }

    public String buildRoleAssignedSubject(String roleName) {
        return "Nuevo rol asignado - " + formatRoleName(roleName);
    }

    public String buildRoleAssignedHtml(String fullName, String roleName, String systemUrl) {
        return wrapHtml("""
                <h2>Nuevo rol asignado</h2>
                <p>Hola <strong>%s</strong>.</p>
                <p>Un administrador te ha asignado un nuevo rol dentro del Sistema de Gestión de Convenios UCC.</p>
                <p>Rol asignado: <strong>%s</strong></p>
                <p>Desde ahora podrás acceder a las funciones asociadas a este rol cuando inicies sesión nuevamente.</p>
                <p>Si ya tenías una sesión abierta, cierra sesión y vuelve a ingresar para actualizar tus permisos.</p>
                <p><a href="%s">Abrir sistema</a></p>
                """.formatted(
                escapeHtml(fullName),
                escapeHtml(formatRoleName(roleName)),
                escapeHtml(systemUrl)
        ));
    }

    public String buildCompanyDocumentUploadSubject(String convenioCode) {
        return "Carga de documentos para convenio " + convenioCode;
    }

    public String buildCompanyDocumentUploadHtml(
            String convenioCode,
            String companyName,
            String uploadUrl,
            int expirationDays,
            List<String> requiredDocuments
    ) {
        return wrapHtml("""
                <h2>Solicitud de documentos</h2>
                <p>Cordial saludo.</p>
                <p>La Universidad Cooperativa de Colombia solicita cargar documentos para el convenio <strong>%s</strong>.</p>
                <p>Empresa: <strong>%s</strong></p>
                <p>Documentos solicitados:</p>
                %s
                <p>Ingrese al siguiente enlace para cargar la información:</p>
                <p><a href="%s" style="display: inline-block; padding: 10px 14px; background: #003366; color: #ffffff; text-decoration: none; border-radius: 6px;">Cargar documentos</a></p>
                <p>Este enlace vence en <strong>%d días</strong>.</p>
                """.formatted(
                escapeHtml(convenioCode),
                escapeHtml(companyName),
                buildList(requiredDocuments),
                escapeHtml(uploadUrl),
                expirationDays
        ));
    }

    public String buildResponsibleDocumentsReceivedSubject(String convenioCode) {
        return "Documentos recibidos - convenio " + convenioCode;
    }

    public String buildResponsibleDocumentsReceivedHtml(String convenioCode, String companyName, String systemUrl) {
        return wrapHtml("""
                <h2>Documentos de empresa recibidos</h2>
                <p>La empresa <strong>%s</strong> cargó documentos externos para el convenio <strong>%s</strong>.</p>
                <p>Ingresa al sistema para revisarlos y aprobarlos u observarlos.</p>
                <p><a href="%s">Abrir sistema</a></p>
                """.formatted(
                escapeHtml(companyName),
                escapeHtml(convenioCode),
                escapeHtml(systemUrl)
        ));
    }

    public String buildEarlyCorrectionLimitSubject(String convenioCode) {
        return "Límite de correcciones tempranas - convenio " + convenioCode;
    }

    public String buildEarlyCorrectionLimitHtml(String convenioCode, String systemUrl) {
        return wrapHtml("""
                <h2>Límite de correcciones tempranas alcanzado</h2>
                <p>El convenio <strong>%s</strong> llegó a 6 correcciones tempranas.</p>
                <p>El responsable debe decidir si hace una última revisión, si radica cuando esté listo o si descarta el proceso.</p>
                <p><a href="%s">Abrir sistema</a></p>
                """.formatted(
                escapeHtml(convenioCode),
                escapeHtml(systemUrl)
        ));
    }

    public String buildEarlyDocumentProcessDiscardedSubject(String convenioCode) {
        return "Convenio descartado en correcciones tempranas - " + convenioCode;
    }

    public String buildEarlyDocumentProcessDiscardedHtml(String convenioCode, String comment, String systemUrl) {
        return wrapHtml("""
                <h2>Proceso documental descartado</h2>
                <p>El convenio <strong>%s</strong> fue descartado durante correcciones tempranas.</p>
                <p><strong>Motivo:</strong> %s</p>
                <p><a href="%s">Abrir sistema</a></p>
                """.formatted(
                escapeHtml(convenioCode),
                escapeHtml(comment),
                escapeHtml(systemUrl)
        ));
    }

    public String buildReviewerAssignedSubject(String convenioCode, ConvenioStage stage) {
        return "Nueva etapa asignada: " + stage.name() + " - convenio " + convenioCode;
    }

    public String buildReviewerAssignedHtml(
            String convenioCode,
            String companyName,
            ConvenioStage stage,
            LocalDateTime dueAt,
            String systemUrl
    ) {
        String dueAtText = dueAt == null ? "Sin fecha límite registrada" : dueAt.format(DATE_TIME_FORMATTER);

        return wrapHtml("""
                <h2>Nueva etapa de revisión asignada</h2>
                <p>Se te asignó una etapa de revisión para el convenio <strong>%s</strong>.</p>
                <p>Empresa: <strong>%s</strong></p>
                <p>Etapa: <strong>%s</strong></p>
                <p>Fecha límite: <strong>%s</strong></p>
                <p>Ingresa al sistema para revisar el convenio y tomar una decisión.</p>
                <p><a href="%s">Abrir sistema</a></p>
                """.formatted(
                escapeHtml(convenioCode),
                escapeHtml(companyName),
                escapeHtml(stage.name()),
                escapeHtml(dueAtText),
                escapeHtml(systemUrl)
        ));
    }

    private String wrapHtml(String bodyContent) {
        return """
                <html>
                    <body style="font-family: Arial, sans-serif; color: #222; line-height: 1.45;">
                        %s
                        <hr>
                        <p style="font-size: 12px; color: #666;">
                            Este correo fue generado automáticamente por el Sistema de Gestión de Convenios UCC. No respondas a este mensaje.
                        </p>
                    </body>
                </html>
                """.formatted(bodyContent);
    }

    private String buildList(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "<p>No se especificaron documentos.</p>";
        }

        StringBuilder builder = new StringBuilder("<ul>");
        for (String item : items) {
            builder.append("<li>").append(escapeHtml(item)).append("</li>");
        }
        builder.append("</ul>");
        return builder.toString();
    }

    private String formatRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return "Rol no especificado";
        }

        return switch (roleName.trim().toUpperCase()) {
            case "SOLICITANTE" -> "Solicitante";
            case "ADMIN" -> "Administrador";
            case "PROFESOR" -> "Profesor";
            case "GESTOR_PROYECCION" -> "Gestor de Proyección Social";
            case "REVISOR_JURIDICO" -> "Revisor Jurídico";
            case "RECTORIA" -> "Rectoría";
            case "RECTOR_MEDELLIN" -> "Rector sede Medellín";
            default -> roleName.trim();
        };
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}