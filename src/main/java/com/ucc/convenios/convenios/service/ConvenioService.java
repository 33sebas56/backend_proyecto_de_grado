package com.ucc.convenios.convenios.service;

import com.ucc.convenios.approvals.service.ApprovalService;
import com.ucc.convenios.companies.entity.Company;
import com.ucc.convenios.companies.repository.CompanyRepository;
import com.ucc.convenios.companydocuments.service.CompanyDocumentWorkflowService;
import com.ucc.convenios.convenios.dto.ConvenioGeneratedDocumentResponse;
import com.ucc.convenios.convenios.dto.ConvenioResponse;
import com.ucc.convenios.convenios.dto.ConvenioStatusHistoryResponse;
import com.ucc.convenios.convenios.dto.ConvenioVersionResponse;
import com.ucc.convenios.convenios.dto.CreateConvenioRequest;
import com.ucc.convenios.convenios.dto.UpdateConvenioRequest;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioGeneratedDocument;
import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.convenios.repository.ConvenioRepository;
import com.ucc.convenios.convenios.repository.ConvenioStatusHistoryRepository;
import com.ucc.convenios.convenios.repository.ConvenioVersionRepository;
import com.ucc.convenios.documents.pdf.PdfGenerationService;
import com.ucc.convenios.documents.storage.FileStorageService;
import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.shared.enums.CompanyStatus;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.shared.enums.ConvenioType;
import com.ucc.convenios.shared.enums.ConvenioVersionReason;
import com.ucc.convenios.shared.enums.ConvenioVersionStatus;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.Objects;
import java.util.Set;
import java.util.List;
import java.util.UUID;

@Service
public class ConvenioService {

    private static final Set<ConvenioStatus> EDITABLE_BEFORE_REVIEW_STATUSES = Set.of(
            ConvenioStatus.BORRADOR,
            ConvenioStatus.EMPRESA_PENDIENTE,
            ConvenioStatus.PENDIENTE_DOCUMENTOS_EMPRESA,
            ConvenioStatus.DOCUMENTOS_EMPRESA_RECIBIDOS,
            ConvenioStatus.DOCUMENTOS_OBSERVADOS_EMPRESA,
            ConvenioStatus.DOCUMENTOS_APROBADOS,
            ConvenioStatus.LISTO_PARA_RADICAR
    );

    private final ConvenioRepository convenioRepository;
    private final ConvenioVersionRepository convenioVersionRepository;
    private final ConvenioStatusHistoryRepository convenioStatusHistoryRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ApprovalService approvalService;
    private final PdfGenerationService pdfGenerationService;
    private final FileStorageService fileStorageService;
    private final ConvenioDocumentService convenioDocumentService;
    private final CompanyDocumentWorkflowService companyDocumentWorkflowService;

    public ConvenioService(
            ConvenioRepository convenioRepository,
            ConvenioVersionRepository convenioVersionRepository,
            ConvenioStatusHistoryRepository convenioStatusHistoryRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            ApprovalService approvalService,
            PdfGenerationService pdfGenerationService,
            FileStorageService fileStorageService,
            ConvenioDocumentService convenioDocumentService,
            CompanyDocumentWorkflowService companyDocumentWorkflowService
    ) {
        this.convenioRepository = convenioRepository;
        this.convenioVersionRepository = convenioVersionRepository;
        this.convenioStatusHistoryRepository = convenioStatusHistoryRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.approvalService = approvalService;
        this.pdfGenerationService = pdfGenerationService;
        this.fileStorageService = fileStorageService;
        this.convenioDocumentService = convenioDocumentService;
        this.companyDocumentWorkflowService = companyDocumentWorkflowService;
    }

    @Transactional
    public ConvenioResponse createConvenio(CreateConvenioRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        validateCanCreateConvenio(currentUser);

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        if (company.getStatus() != CompanyStatus.VALIDADA) {
            throw new BadRequestException("Solo se pueden crear convenios con empresas validadas");
        }

        if (request.getDurationMonths() == null || request.getDurationMonths() <= 0) {
            throw new BadRequestException("La duración del convenio debe ser mayor a 0 meses");
        }

        ConvenioType convenioType = request.getConvenioType() == null
                ? ConvenioType.MARCO
                : request.getConvenioType();

        Convenio convenio = new Convenio();
        convenio.setCode(generateConvenioCode());
        convenio.setCompany(company);
        convenio.setCreatedBy(currentUser);
        convenio.setConvenioType(convenioType);
        convenio.setCurrentStatus(ConvenioStatus.BORRADOR);
        convenio.setStartDate(null);
        convenio.setEndDate(null);

        Convenio savedConvenio = convenioRepository.save(convenio);

        ConvenioVersion version = new ConvenioVersion();
        version.setConvenio(savedConvenio);
        version.setVersionNumber(1);
        version.setTitle(request.getTitle());
        version.setObjective(request.getObjective());
        version.setDescription(request.getDescription());
        version.setDurationMonths(request.getDurationMonths());
        version.setStartDate(null);
        version.setEndDate(null);
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
                "Convenio creado en estado borrador. Tipo: " + convenioType.getDisplayName()
                        + ". Duración propuesta: " + request.getDurationMonths() + " meses."
                        + " Firmante de rectoría: " + convenioType.getRectorSignerLabel(),
                currentUser
        );

        return ConvenioResponse.fromEntity(updatedConvenio);
    }

    @Transactional
    public ConvenioResponse updateConvenioBeforeReview(UUID convenioId, UpdateConvenioRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioForUpdate(convenioId);

        validateCanUpdateConvenioBeforeReview(convenio, currentUser);
        validateEditableBeforeReview(convenio);

        ConvenioVersion currentVersion = convenio.getCurrentVersion();
        if (currentVersion == null) {
            throw new BadRequestException("El convenio no tiene una versión actual para editar");
        }

        StringBuilder changes = new StringBuilder();

        if (request.getCompanyId() != null && !request.getCompanyId().equals(convenio.getCompany().getId())) {
            if (convenio.getCurrentStatus() != ConvenioStatus.BORRADOR) {
                throw new BadRequestException("La empresa solo se puede cambiar mientras el convenio esté en BORRADOR");
            }

            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

            if (company.getStatus() != CompanyStatus.VALIDADA) {
                throw new BadRequestException("Solo se pueden asociar convenios a empresas validadas");
            }

            convenio.setCompany(company);
            appendChange(changes, "empresa");
        }

        if (request.getConvenioType() != null && request.getConvenioType() != convenio.getConvenioType()) {
            convenio.setConvenioType(request.getConvenioType());
            appendChange(changes, "tipo de convenio");
        }

        if (request.getTitle() != null) {
            String title = requireText(request.getTitle(), "El título del convenio no puede estar vacío");
            if (!Objects.equals(currentVersion.getTitle(), title)) {
                currentVersion.setTitle(title);
                appendChange(changes, "título");
            }
        }

        if (request.getObjective() != null) {
            String objective = requireText(request.getObjective(), "El objeto del convenio no puede estar vacío");
            if (!Objects.equals(currentVersion.getObjective(), objective)) {
                currentVersion.setObjective(objective);
                appendChange(changes, "objeto");
            }
        }

        if (request.getDescription() != null) {
            String description = normalizeOptionalText(request.getDescription());
            if (!Objects.equals(currentVersion.getDescription(), description)) {
                currentVersion.setDescription(description);
                appendChange(changes, "descripción");
            }
        }

        if (request.getDurationMonths() != null) {
            if (request.getDurationMonths() <= 0) {
                throw new BadRequestException("La duración del convenio debe ser mayor a 0 meses");
            }

            if (!Objects.equals(currentVersion.getDurationMonths(), request.getDurationMonths())) {
                currentVersion.setDurationMonths(request.getDurationMonths());
                appendChange(changes, "duración");
            }
        }

        if (request.getExternalEntityObligations() != null) {
            String obligations = normalizeOptionalText(request.getExternalEntityObligations());
            if (!Objects.equals(currentVersion.getExternalEntityObligations(), obligations)) {
                currentVersion.setExternalEntityObligations(obligations);
                appendChange(changes, "obligaciones de la empresa");
            }
        }

        if (request.getUniversityObligations() != null) {
            String obligations = normalizeOptionalText(request.getUniversityObligations());
            if (!Objects.equals(currentVersion.getUniversityObligations(), obligations)) {
                currentVersion.setUniversityObligations(obligations);
                appendChange(changes, "obligaciones de la universidad");
            }
        }

        if (request.getEstimatedValue() != null) {
            if (request.getEstimatedValue().signum() < 0) {
                throw new BadRequestException("El valor estimado no puede ser negativo");
            }

            if (!Objects.equals(currentVersion.getEstimatedValue(), request.getEstimatedValue())) {
                currentVersion.setEstimatedValue(request.getEstimatedValue());
                appendChange(changes, "valor estimado");
            }
        }

        if (changes.length() == 0) {
            return ConvenioResponse.fromEntity(convenio);
        }

        convenioVersionRepository.save(currentVersion);
        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                convenio.getCurrentStatus(),
                convenio.getCurrentStatus(),
                convenio.getCurrentStage(),
                convenio.getCurrentStage(),
                "Convenio editado antes de enviarse a revisión. Campos ajustados: " + changes,
                currentUser
        );

        return ConvenioResponse.fromEntity(savedConvenio);
    }

    @Transactional(readOnly = true)
    public List<ConvenioResponse> findAll() {
        return convenioRepository.findAllByOrderByUpdatedAtDesc()
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

        if (convenio.getCurrentStatus() != ConvenioStatus.LISTO_PARA_RADICAR &&
                convenio.getCurrentStatus() != ConvenioStatus.EN_CORRECCION) {
            throw new BadRequestException("El convenio solo se puede radicar cuando esté LISTO_PARA_RADICAR o en corrección formal");
        }

        if (!companyDocumentWorkflowService.hasApprovedCompanyDocuments(convenio)) {
            throw new BadRequestException("No se puede radicar: faltan documentos de empresa aprobados");
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

        ConvenioStage firstStage = resolveFirstFormalStage(convenio.getCreatedBy());

        convenio.setCurrentStatus(ConvenioStatus.RADICADO);
        convenio.setCurrentStage(firstStage);

        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.RADICADO,
                previousStage,
                firstStage,
                buildSubmitHistoryComment(savedConvenio, firstStage),
                currentUser
        );

        approvalService.createInitialApprovalRound(savedConvenio, currentVersion, firstStage);

        return ConvenioResponse.fromEntity(savedConvenio);
    }

    @Transactional
    public ConvenioResponse formalizeConvenio(UUID convenioId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        validateCanFormalizeConvenio(currentUser);

        Convenio convenio = getConvenioForUpdate(convenioId);

        if (convenio.getCurrentStatus() != ConvenioStatus.APROBADO_PARA_FIRMA) {
            throw new BadRequestException("Solo se pueden formalizar convenios en estado APROBADO_PARA_FIRMA");
        }

        ConvenioVersion currentVersion = convenio.getCurrentVersion();

        if (currentVersion == null) {
            throw new BadRequestException("El convenio no tiene una versión actual");
        }

        Integer durationMonths = currentVersion.getDurationMonths();

        if (durationMonths == null || durationMonths <= 0) {
            throw new BadRequestException("El convenio no tiene una duración válida para formalizar");
        }

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        ConvenioStage previousStage = convenio.getCurrentStage();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(durationMonths);

        convenio.setStartDate(startDate);
        convenio.setEndDate(endDate);
        convenio.setCurrentStatus(ConvenioStatus.FORMALIZADO);
        convenio.setCurrentStage(null);

        currentVersion.setStartDate(startDate);
        currentVersion.setEndDate(endDate);
        convenioVersionRepository.save(currentVersion);

        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.FORMALIZADO,
                previousStage,
                null,
                "Convenio formalizado por Proyección Social. Fecha de inicio: "
                        + startDate
                        + ". Fecha de finalización: "
                        + endDate
                        + ". Duración aplicada: "
                        + durationMonths
                        + " meses.",
                currentUser
        );

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

        String previewPath = fileStorageService.savePreviewPdf(convenio.getId(), pdfBytes);

        return previewPath;
    }

    @Transactional(readOnly = true)
    public byte[] getPreviewPdf(UUID convenioId) {
        Convenio convenio = getConvenioById(convenioId);

        String previewPath = fileStorageService.getPreviewPdfStoragePath(convenio.getId());

        if (!fileStorageService.exists(previewPath)) {
            throw new ResourceNotFoundException("No existe vista previa PDF para este convenio");
        }

        return fileStorageService.readFile(previewPath);
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

        return fileStorageService.readFile(version.getGeneratedPdfStoragePath());
    }

    private void validateCanCreateConvenio(User user) {
        Set<String> allowedRoles = Set.of("ADMIN", "PROFESOR", "GESTOR_PROYECCION");

        boolean allowed = userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .anyMatch(allowedRoles::contains);

        if (!allowed) {
            throw new BadRequestException("Solo ADMIN, PROFESOR o GESTOR_PROYECCION pueden crear convenios");
        }
    }

    private void validateCanUpdateConvenioBeforeReview(Convenio convenio, User user) {
        if (convenio.getCreatedBy().getId().equals(user.getId())) {
            return;
        }

        Set<String> allowedRoles = Set.of("ADMIN", "GESTOR_PROYECCION");

        boolean allowed = userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .anyMatch(allowedRoles::contains);

        if (!allowed) {
            throw new BadRequestException("Solo el responsable del convenio, Proyección Social o ADMIN pueden editar el convenio antes de enviarlo a revisión");
        }
    }

    private void validateCanFormalizeConvenio(User user) {
        Set<String> allowedRoles = Set.of("ADMIN", "GESTOR_PROYECCION");

        boolean allowed = userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .anyMatch(allowedRoles::contains);

        if (!allowed) {
            throw new BadRequestException("Solo ADMIN o GESTOR_PROYECCION pueden formalizar convenios");
        }
    }

    private void validateEditableBeforeReview(Convenio convenio) {
        if (!EDITABLE_BEFORE_REVIEW_STATUSES.contains(convenio.getCurrentStatus())) {
            throw new BadRequestException("El convenio solo se puede editar antes de enviarlo a revisión formal");
        }
    }

    private ConvenioStage resolveFirstFormalStage(User creator) {
        boolean createdByProjection = userRoleRepository.findByUser(creator)
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .anyMatch("GESTOR_PROYECCION"::equals);

        if (createdByProjection) {
            return ConvenioStage.JURIDICA;
        }

        return ConvenioStage.PROYECCION;
    }

    private String buildSubmitHistoryComment(Convenio convenio, ConvenioStage firstStage) {
        String rectorSignerLabel = convenio.getRectorSignerLabel();

        if (firstStage == ConvenioStage.JURIDICA) {
            return "Convenio radicado por Proyección Social, PDF oficial generado y enviado a Jurídica. "
                    + "Firmante final esperado en Rectoría: " + rectorSignerLabel;
        }

        return "Convenio radicado, PDF oficial generado y enviado a Proyección Social. "
                + "Firmante final esperado en Rectoría: " + rectorSignerLabel;
    }

    private void appendChange(StringBuilder changes, String fieldName) {
        if (changes.length() > 0) {
            changes.append(", ");
        }
        changes.append(fieldName);
    }

    private String requireText(String value, String errorMessage) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new BadRequestException(errorMessage);
        }
        return normalized;
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
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

    private Convenio getConvenioForUpdate(UUID convenioId) {
        return convenioRepository.findForUpdateById(convenioId)
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
