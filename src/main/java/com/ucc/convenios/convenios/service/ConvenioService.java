package com.ucc.convenios.convenios.service;

import com.ucc.convenios.companies.entity.Company;
import com.ucc.convenios.companies.repository.CompanyRepository;
import com.ucc.convenios.convenios.dto.*;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.convenios.repository.ConvenioRepository;
import com.ucc.convenios.convenios.repository.ConvenioStatusHistoryRepository;
import com.ucc.convenios.convenios.repository.ConvenioVersionRepository;
import com.ucc.convenios.shared.enums.CompanyStatus;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.shared.enums.ConvenioVersionReason;
import com.ucc.convenios.shared.enums.ConvenioVersionStatus;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ucc.convenios.approvals.service.ApprovalService;
import com.ucc.convenios.documents.pdf.PdfGenerationService;
import com.ucc.convenios.documents.storage.LocalFileStorageService;
import com.ucc.convenios.convenios.entity.ConvenioGeneratedDocument;
import java.nio.file.Path;
import com.ucc.convenios.convenios.dto.ConvenioGeneratedDocumentResponse;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class ConvenioService {

    private final ConvenioRepository convenioRepository;
    private final ConvenioVersionRepository convenioVersionRepository;
    private final ConvenioStatusHistoryRepository convenioStatusHistoryRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ApprovalService approvalService;
    private final PdfGenerationService pdfGenerationService;
    private final LocalFileStorageService localFileStorageService;
    private final ConvenioDocumentService convenioDocumentService;

    public ConvenioService(
            ConvenioRepository convenioRepository,
            ConvenioVersionRepository convenioVersionRepository,
            ConvenioStatusHistoryRepository convenioStatusHistoryRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository,
            ApprovalService approvalService,
            PdfGenerationService pdfGenerationService,
            LocalFileStorageService localFileStorageService,
            ConvenioDocumentService convenioDocumentService
    ) {
        this.convenioRepository = convenioRepository;
        this.convenioVersionRepository = convenioVersionRepository;
        this.convenioStatusHistoryRepository = convenioStatusHistoryRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.approvalService = approvalService;
        this.pdfGenerationService = pdfGenerationService;
        this.localFileStorageService = localFileStorageService;
        this.convenioDocumentService = convenioDocumentService;
    }

    @Transactional
    public ConvenioResponse createConvenio(CreateConvenioRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        if (company.getStatus() != CompanyStatus.VALIDADA) {
            throw new BadRequestException("Solo se pueden crear convenios con empresas validadas");
        }

        Convenio convenio = new Convenio();
        convenio.setCode(generateConvenioCode());
        convenio.setCompany(company);
        convenio.setCreatedBy(currentUser);
        convenio.setCurrentStatus(ConvenioStatus.BORRADOR);
        convenio.setStartDate(request.getStartDate());
        convenio.setEndDate(request.getEndDate());

        Convenio savedConvenio = convenioRepository.save(convenio);

        ConvenioVersion version = new ConvenioVersion();
        version.setConvenio(savedConvenio);
        version.setVersionNumber(1);
        version.setTitle(request.getTitle());
        version.setObjective(request.getObjective());
        version.setDescription(request.getDescription());
        version.setDurationMonths(request.getDurationMonths());
        version.setStartDate(request.getStartDate());
        version.setEndDate(request.getEndDate());
        version.setExternalEntityObligations(request.getExternalEntityObligations());
        version.setUniversityObligations(request.getUniversityObligations());
        version.setEstimatedValue(request.getEstimatedValue());
        version.setStatus(ConvenioVersionStatus.BORRADOR);
        version.setReason(ConvenioVersionReason.CREACION_INICIAL);
        version.setCreatedBy(currentUser);

        ConvenioVersion savedVersion = convenioVersionRepository.save(version);

        savedConvenio.setCurrentVersion(savedVersion);
        Convenio updatedConvenio = convenioRepository.save(savedConvenio);

        registerStatusHistory(
                updatedConvenio,
                null,
                ConvenioStatus.BORRADOR,
                null,
                null,
                "Convenio creado en estado borrador",
                currentUser
        );

        return ConvenioResponse.fromEntity(updatedConvenio);
    }

    @Transactional(readOnly = true)
    public List<ConvenioResponse> findAll() {
        return convenioRepository.findAll()
                .stream()
                .map(ConvenioResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConvenioResponse findById(UUID id) {
        Convenio convenio = getConvenioWithDetails(id);
        return ConvenioResponse.fromEntity(convenio);
    }

    @Transactional(readOnly = true)
    public List<ConvenioVersionResponse> findVersions(UUID convenioId) {
        Convenio convenio = getConvenioById(convenioId);

        return convenioVersionRepository.findByConvenioOrderByVersionNumberDesc(convenio)
                .stream()
                .map(ConvenioVersionResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConvenioStatusHistoryResponse> findStatusHistory(UUID convenioId) {
        Convenio convenio = getConvenioById(convenioId);

        return convenioStatusHistoryRepository.findByConvenioOrderByPerformedAtDesc(convenio)
                .stream()
                .map(ConvenioStatusHistoryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ConvenioResponse submitConvenio(UUID convenioId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);

        if (convenio.getCurrentStatus() != ConvenioStatus.BORRADOR &&
                convenio.getCurrentStatus() != ConvenioStatus.EN_CORRECCION) {
            throw new BadRequestException("El convenio no se puede radicar desde su estado actual");
        }

        ConvenioVersion currentVersion = convenio.getCurrentVersion();

        if (currentVersion == null) {
            throw new BadRequestException("El convenio no tiene una versión actual");
        }

        ConvenioGeneratedDocument radicadoDocument = convenioDocumentService.createRadicadoDocument(
                convenio,
                currentVersion,
                currentUser
        );

        currentVersion.setGeneratedPdfStoragePath(radicadoDocument.getStoragePath());
        currentVersion.setGeneratedPdfUrl(radicadoDocument.getUrl());
        currentVersion.setStatus(ConvenioVersionStatus.VIGENTE);
        convenioVersionRepository.save(currentVersion);

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        ConvenioStage previousStage = convenio.getCurrentStage();

        convenio.setCurrentStatus(ConvenioStatus.RADICADO);
        convenio.setCurrentStage(ConvenioStage.PROYECCION);

        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.RADICADO,
                previousStage,
                ConvenioStage.PROYECCION,
                "Convenio radicado, PDF oficial generado y enviado a Proyección Social",
                currentUser
        );

        approvalService.createInitialApprovalRound(savedConvenio, currentVersion);

        return ConvenioResponse.fromEntity(savedConvenio);
    }
    @Transactional
    public String generatePreviewPdf(UUID convenioId, Authentication authentication) {
        getCurrentUser(authentication);

        Convenio convenio = getConvenioWithDetails(convenioId);

        if (convenio.getCurrentVersion() == null) {
            throw new BadRequestException("El convenio no tiene una versión actual");
        }

        byte[] pdfBytes = pdfGenerationService.generateConvenioPreviewPdf(
                convenio,
                convenio.getCurrentVersion()
        );

        Path previewPath = localFileStorageService.savePreviewPdf(convenio.getId(), pdfBytes);

        return previewPath.toString();
    }

    @Transactional(readOnly = true)
    public byte[] getPreviewPdf(UUID convenioId) {
        Convenio convenio = getConvenioById(convenioId);

        Path previewPath = Path.of(
                "storage",
                "convenios",
                "previews",
                convenio.getId().toString(),
                "preview.pdf"
        );

        if (!localFileStorageService.exists(previewPath.toString())) {
            throw new ResourceNotFoundException("No existe vista previa PDF para este convenio");
        }

        return localFileStorageService.readFile(previewPath.toString());
    }

    @Transactional(readOnly = true)
    public List<ConvenioGeneratedDocumentResponse> findGeneratedDocuments(UUID convenioId) {
        return convenioDocumentService.findDocumentsByConvenio(convenioId);
    }

    @Transactional(readOnly = true)
    public byte[] getGeneratedDocumentPdf(UUID convenioId, UUID documentId) {
        return convenioDocumentService.getDocumentPdf(convenioId, documentId);
    }

    @Transactional(readOnly = true)
    public byte[] getOfficialPdf(UUID convenioId, UUID versionId) {
        Convenio convenio = getConvenioById(convenioId);

        ConvenioVersion version = convenioVersionRepository.findWithDetailsById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Versión del convenio no encontrada"));

        if (!version.getConvenio().getId().equals(convenio.getId())) {
            throw new BadRequestException("La versión no pertenece a este convenio");
        }

        if (version.getGeneratedPdfStoragePath() == null || version.getGeneratedPdfStoragePath().isBlank()) {
            throw new ResourceNotFoundException("La versión no tiene PDF generado");
        }

        return localFileStorageService.readFile(version.getGeneratedPdfStoragePath());
    }

    private void registerStatusHistory(
            Convenio convenio,
            ConvenioStatus previousStatus,
            ConvenioStatus newStatus,
            ConvenioStage previousStage,
            ConvenioStage newStage,
            String comment,
            User performedBy
    ) {
        ConvenioStatusHistory history = new ConvenioStatusHistory();
        history.setConvenio(convenio);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setPreviousStage(previousStage);
        history.setNewStage(newStage);
        history.setComment(comment);
        history.setPerformedBy(performedBy);

        convenioStatusHistoryRepository.save(history);
    }

    private Convenio getConvenioById(UUID convenioId) {
        return convenioRepository.findById(convenioId)
                .orElseThrow(() -> new ResourceNotFoundException("Convenio no encontrado"));
    }

    private Convenio getConvenioWithDetails(UUID convenioId) {
        return convenioRepository.findWithDetailsById(convenioId)
                .orElseThrow(() -> new ResourceNotFoundException("Convenio no encontrado"));
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private String generateConvenioCode() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "CONV-" + year + "-";

        long count = convenioRepository.count() + 1;
        String sequence = String.format("%04d", count);

        String code = prefix + sequence;

        while (convenioRepository.existsByCode(code)) {
            count++;
            sequence = String.format("%04d", count);
            code = prefix + sequence;
        }

        return code;
    }
}