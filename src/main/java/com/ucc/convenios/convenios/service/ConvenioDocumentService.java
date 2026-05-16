package com.ucc.convenios.convenios.service;

import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.convenios.dto.ConvenioGeneratedDocumentResponse;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioGeneratedDocument;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.convenios.repository.ConvenioGeneratedDocumentRepository;
import com.ucc.convenios.convenios.repository.ConvenioRepository;
import com.ucc.convenios.documents.pdf.PdfGenerationService;
import com.ucc.convenios.documents.storage.FileStorageService;
import com.ucc.convenios.shared.enums.ConvenioGeneratedDocumentType;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConvenioDocumentService {

    private final ConvenioGeneratedDocumentRepository documentRepository;
    private final ConvenioRepository convenioRepository;
    private final PdfGenerationService pdfGenerationService;
    private final FileStorageService fileStorageService;

    public ConvenioDocumentService(
            ConvenioGeneratedDocumentRepository documentRepository,
            ConvenioRepository convenioRepository,
            PdfGenerationService pdfGenerationService,
            FileStorageService fileStorageService
    ) {
        this.documentRepository = documentRepository;
        this.convenioRepository = convenioRepository;
        this.pdfGenerationService = pdfGenerationService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public ConvenioGeneratedDocument createRadicadoDocument(
            Convenio convenio,
            ConvenioVersion version,
            User generatedBy
    ) {
        byte[] pdfBytes = pdfGenerationService.generateOfficialConvenioPdf(convenio, version);

        String fileName = "version-" + version.getVersionNumber() + "-radicado.pdf";

        String storagePath = fileStorageService.saveOfficialPdf(
                convenio.getId(),
                fileName,
                pdfBytes
        );

        return saveDocumentRecord(
                convenio,
                version,
                null,
                ConvenioGeneratedDocumentType.RADICADO,
                null,
                fileName,
                storagePath,
                generatedBy,
                "Documento radicado generado al enviar el convenio a revisión."
        );
    }

    @Transactional
    public ConvenioGeneratedDocument createFinalApprovedDocument(
            Convenio convenio,
            ConvenioVersion version,
            List<ApprovalStep> approvalSteps,
            User generatedBy
    ) {
        byte[] pdfBytes = pdfGenerationService.generateFinalConvenioPdf(convenio, version, approvalSteps);

        String fileName = "version-" + version.getVersionNumber() + "-final-aprobado.pdf";

        String storagePath = fileStorageService.saveOfficialPdf(
                convenio.getId(),
                fileName,
                pdfBytes
        );

        return saveDocumentRecord(
                convenio,
                version,
                null,
                ConvenioGeneratedDocumentType.FINAL_APROBADO,
                null,
                fileName,
                storagePath,
                generatedBy,
                "Documento final aprobado con constancias de revisión."
        );
    }

    @Transactional
    public ConvenioGeneratedDocument createCorrectionDocument(
            Convenio convenio,
            ConvenioVersion version,
            ApprovalStep step,
            User generatedBy,
            String comment
    ) {
        byte[] pdfBytes = pdfGenerationService.generateDecisionPdf(
                convenio,
                version,
                step,
                ConvenioGeneratedDocumentType.CORRECCION_SOLICITADA,
                comment
        );

        String fileName = "version-" + version.getVersionNumber()
                + "-correccion-" + step.getStage().name().toLowerCase() + ".pdf";

        String storagePath = fileStorageService.saveOfficialPdf(
                convenio.getId(),
                fileName,
                pdfBytes
        );

        return saveDocumentRecord(
                convenio,
                version,
                step,
                ConvenioGeneratedDocumentType.CORRECCION_SOLICITADA,
                step.getStage(),
                fileName,
                storagePath,
                generatedBy,
                comment
        );
    }

    @Transactional
    public ConvenioGeneratedDocument createRejectedDocument(
            Convenio convenio,
            ConvenioVersion version,
            ApprovalStep step,
            User generatedBy,
            String comment
    ) {
        byte[] pdfBytes = pdfGenerationService.generateDecisionPdf(
                convenio,
                version,
                step,
                ConvenioGeneratedDocumentType.RECHAZADO,
                comment
        );

        String fileName = "version-" + version.getVersionNumber()
                + "-rechazado-" + step.getStage().name().toLowerCase() + ".pdf";

        String storagePath = fileStorageService.saveOfficialPdf(
                convenio.getId(),
                fileName,
                pdfBytes
        );

        return saveDocumentRecord(
                convenio,
                version,
                step,
                ConvenioGeneratedDocumentType.RECHAZADO,
                step.getStage(),
                fileName,
                storagePath,
                generatedBy,
                comment
        );
    }

    @Transactional
    public ConvenioGeneratedDocument createExpiredReviewDocument(
            Convenio convenio,
            ConvenioVersion version,
            ApprovalStep step,
            User generatedBy,
            String comment
    ) {
        byte[] pdfBytes = pdfGenerationService.generateDecisionPdf(
                convenio,
                version,
                step,
                ConvenioGeneratedDocumentType.REVISION_VENCIDA,
                comment
        );

        String fileName = "version-" + version.getVersionNumber()
                + "-revision-vencida-" + step.getStage().name().toLowerCase() + ".pdf";

        String storagePath = fileStorageService.saveOfficialPdf(
                convenio.getId(),
                fileName,
                pdfBytes
        );

        return saveDocumentRecord(
                convenio,
                version,
                step,
                ConvenioGeneratedDocumentType.REVISION_VENCIDA,
                step.getStage(),
                fileName,
                storagePath,
                generatedBy,
                comment
        );
    }

    @Transactional(readOnly = true)
    public List<ConvenioGeneratedDocumentResponse> findDocumentsByConvenio(UUID convenioId) {
        Convenio convenio = convenioRepository.findById(convenioId)
                .orElseThrow(() -> new ResourceNotFoundException("Convenio no encontrado"));

        return documentRepository.findByConvenioOrderByGeneratedAtDesc(convenio)
                .stream()
                .map(ConvenioGeneratedDocumentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public byte[] getDocumentPdf(UUID convenioId, UUID documentId) {
        ConvenioGeneratedDocument document = documentRepository.findWithDetailsById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento generado no encontrado"));

        if (!document.getConvenio().getId().equals(convenioId)) {
            throw new BadRequestException("El documento no pertenece a este convenio");
        }

        return fileStorageService.readFile(document.getStoragePath());
    }

    private ConvenioGeneratedDocument saveDocumentRecord(
            Convenio convenio,
            ConvenioVersion version,
            ApprovalStep approvalStep,
            ConvenioGeneratedDocumentType documentType,
            ConvenioStage stage,
            String fileName,
            String storagePath,
            User generatedBy,
            String notes
    ) {
        ConvenioGeneratedDocument document = new ConvenioGeneratedDocument();
        document.setConvenio(convenio);
        document.setConvenioVersion(version);
        document.setApprovalStep(approvalStep);
        document.setDocumentType(documentType);
        document.setStage(stage);
        document.setFileName(fileName);
        document.setStoragePath(storagePath);
        document.setUrl("/pending-url");
        document.setGeneratedBy(generatedBy);
        document.setNotes(notes);

        ConvenioGeneratedDocument savedDocument = documentRepository.save(document);

        savedDocument.setUrl("/api/convenios/" + convenio.getId() + "/documents/" + savedDocument.getId() + "/pdf");

        return documentRepository.save(savedDocument);
    }
}