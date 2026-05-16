package com.ucc.convenios.documents.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.shared.enums.ConvenioGeneratedDocumentType;
import com.ucc.convenios.shared.enums.ConvenioType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class PdfGenerationService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generateConvenioPreviewPdf(Convenio convenio, ConvenioVersion version) {
        return generateConvenioPdf(
                convenio,
                version,
                List.of(),
                "VISTA PREVIA DE CONVENIO",
                false
        );
    }

    public byte[] generateOfficialConvenioPdf(Convenio convenio, ConvenioVersion version) {
        return generateConvenioPdf(
                convenio,
                version,
                List.of(),
                "DOCUMENTO RADICADO PARA REVISIÓN",
                false
        );
    }

    public byte[] generateFinalConvenioPdf(
            Convenio convenio,
            ConvenioVersion version,
            List<ApprovalStep> approvalSteps
    ) {
        return generateConvenioPdf(
                convenio,
                version,
                approvalSteps,
                "CONVENIO APROBADO",
                true
        );
    }

    public byte[] generateDecisionPdf(
            Convenio convenio,
            ConvenioVersion version,
            ApprovalStep step,
            ConvenioGeneratedDocumentType documentType,
            String comment
    ) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document(PageSize.LETTER, 50, 50, 45, 45);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            String title = switch (documentType) {
                case CORRECCION_SOLICITADA -> "CONSTANCIA DE CORRECCIÓN SOLICITADA";
                case RECHAZADO -> "CONSTANCIA DE RECHAZO DEL CONVENIO";
                case REVISION_VENCIDA -> "CONSTANCIA DE REVISIÓN VENCIDA";
                default -> "CONSTANCIA DE DECISIÓN DEL CONVENIO";
            };

            addInstitutionalHeader(document, title);
            addBasicInfo(document, convenio, version);

            addSection(document, "Decisión emitida", documentType.name());
            addSection(document, "Etapa responsable", step.getStage().name());
            addSection(document, "Responsable", step.getAssignedUser().getFullName());
            addSection(document, "Correo institucional", step.getAssignedUser().getEmail());
            addSection(document, "Fecha de decisión", LocalDateTime.now().format(DATE_TIME_FORMATTER));
            addSection(document, "Comentario o motivo", comment);

            if (step.getApprovalCode() != null) {
                addSection(document, "Código de decisión", step.getApprovalCode());
            }

            addFooterNote(document);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException("No se pudo generar el PDF de decisión del convenio", exception);
        }
    }

    private byte[] generateConvenioPdf(
            Convenio convenio,
            ConvenioVersion version,
            List<ApprovalStep> approvalSteps,
            String title,
            boolean includeApprovalPage
    ) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document(PageSize.LETTER, 50, 50, 45, 45);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            addInstitutionalHeader(document, title);
            addBasicInfo(document, convenio, version);
            addSection(document, "Objeto del convenio", version.getObjective());
            addSection(document, "Descripción", version.getDescription());
            addSection(document, "Obligaciones de la entidad externa", version.getExternalEntityObligations());
            addSection(document, "Obligaciones de la universidad", version.getUniversityObligations());

            addFooterNote(document);

            if (includeApprovalPage) {
                document.newPage();
                addApprovalPage(document, convenio, approvalSteps);
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException("No se pudo generar el PDF del convenio", exception);
        }
    }

    private void addInstitutionalHeader(Document document, String title) throws Exception {
        Font institutionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
        Font subtitleFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);

        Paragraph institution = new Paragraph("UNIVERSIDAD COOPERATIVA DE COLOMBIA", institutionFont);
        institution.setAlignment(Element.ALIGN_CENTER);
        institution.setSpacingAfter(4);
        document.add(institution);

        Paragraph subtitle = new Paragraph("Sistema de Gestión y Aprobación de Convenios", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(16);
        document.add(subtitle);

        Paragraph mainTitle = new Paragraph(title, titleFont);
        mainTitle.setAlignment(Element.ALIGN_CENTER);
        mainTitle.setSpacingAfter(18);
        document.add(mainTitle);
    }

    private void addBasicInfo(Document document, Convenio convenio, ConvenioVersion version) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(18);
        table.setWidths(new float[]{32, 68});

        addRow(table, "Código del convenio", convenio.getCode());
        addRow(table, "Empresa", convenio.getCompany().getBusinessName());
        addRow(table, "NIT", convenio.getCompany().getNit());
        addRow(table, "Tipo de convenio", getConvenioTypeLabel(convenio));
        addRow(table, "Firmante de rectoría", convenio.getRectorSignerLabel());
        addRow(table, "Título", version.getTitle());
        addRow(table, "Versión", String.valueOf(version.getVersionNumber()));
        addRow(table, "Duración", version.getDurationMonths() == null ? "No especificada" : version.getDurationMonths() + " meses");
        addRow(table, "Valor estimado", formatMoney(version));

        document.add(table);
    }

    private void addSection(Document document, String title, String content) throws Exception {
        Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font contentFont = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph sectionTitle = new Paragraph(title, sectionFont);
        sectionTitle.setSpacingBefore(9);
        sectionTitle.setSpacingAfter(5);
        document.add(sectionTitle);

        Paragraph sectionContent = new Paragraph(
                content == null || content.isBlank() ? "No especificado" : content.trim(),
                contentFont
        );
        sectionContent.setAlignment(Element.ALIGN_JUSTIFIED);
        sectionContent.setSpacingAfter(8);
        document.add(sectionContent);
    }

    private void addApprovalPage(Document document, Convenio convenio, List<ApprovalStep> approvalSteps) throws Exception {
        addInstitutionalHeader(document, "CONSTANCIAS DE REVISIÓN INTERNA");

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingAfter(18);
        table.setWidths(new float[]{18, 18, 26, 38});

        addTableHeader(table, "Etapa");
        addTableHeader(table, "Estado");
        addTableHeader(table, "Responsable");
        addTableHeader(table, "Constancia");

        for (ApprovalStep step : approvalSteps) {
            addTableCell(table, step.getStage().name());
            addTableCell(table, step.getStatus().name());
            addTableCell(table, step.getAssignedUser().getEmail());
            addTableCell(table, step.getSealText() == null ? "Sin constancia" : step.getSealText());
        }

        document.add(table);

        addSection(document, "Regla de firma de Rectoría", buildRectorSignerNote(convenio));

        Paragraph note = new Paragraph(
                "Este documento contiene las constancias internas de revisión registradas por el sistema. " +
                        "No representa firma digital certificada.",
                new Font(Font.HELVETICA, 10, Font.ITALIC)
        );
        note.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(note);
    }

    private void addFooterNote(Document document) throws Exception {
        Paragraph note = new Paragraph(
                "\nDocumento generado automáticamente por el Sistema de Gestión y Aprobación de Convenios.",
                new Font(Font.HELVETICA, 9, Font.ITALIC)
        );
        note.setAlignment(Element.ALIGN_CENTER);
        note.setSpacingBefore(15);
        document.add(note);
    }

    private void addRow(PdfPTable table, String label, String value) {
        addLabelCell(table, label);
        addValueCell(table, value == null || value.isBlank() ? "No especificado" : value);
    }

    private void addLabelCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);
    }

    private void addValueCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setPadding(6);
        cell.setBorder(Rectangle.BOX);
        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 9, Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setPadding(6);
        table.addCell(cell);
    }

    private String getConvenioTypeLabel(Convenio convenio) {
        ConvenioType type = convenio.getConvenioType() == null
                ? ConvenioType.MARCO
                : convenio.getConvenioType();

        return type.getDisplayName();
    }

    private String buildRectorSignerNote(Convenio convenio) {
        return "Según el tipo de convenio registrado, el firmante final correspondiente en Rectoría es: "
                + convenio.getRectorSignerLabel()
                + ". Esta regla se deriva del tipo de convenio seleccionado al crear el registro.";
    }

    private String formatMoney(ConvenioVersion version) {
        if (version.getEstimatedValue() == null) {
            return "No especificado";
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return formatter.format(version.getEstimatedValue());
    }
}