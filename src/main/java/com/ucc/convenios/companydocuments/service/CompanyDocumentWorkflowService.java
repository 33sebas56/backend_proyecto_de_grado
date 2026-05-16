package com.ucc.convenios.companydocuments.service;

import com.ucc.convenios.companydocuments.dto.CompanyDocumentCorrectionRequest;
import com.ucc.convenios.companydocuments.dto.CompanyDocumentDiscardRequest;
import com.ucc.convenios.companydocuments.dto.CompanyDocumentRequestResponse;
import com.ucc.convenios.companydocuments.dto.CompanyDocumentReviewRequest;
import com.ucc.convenios.companydocuments.dto.CompanySubmittedDocumentResponse;
import com.ucc.convenios.companydocuments.dto.PublicCompanyUploadInfoResponse;
import com.ucc.convenios.companydocuments.dto.RequiredCompanyDocumentResponse;
import com.ucc.convenios.companydocuments.entity.CompanyDocumentRequest;
import com.ucc.convenios.companydocuments.entity.CompanySubmittedDocument;
import com.ucc.convenios.companydocuments.entity.CompanyUploadToken;
import com.ucc.convenios.companydocuments.repository.CompanyDocumentRequestRepository;
import com.ucc.convenios.companydocuments.repository.CompanySubmittedDocumentRepository;
import com.ucc.convenios.companydocuments.repository.CompanyUploadTokenRepository;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;
import com.ucc.convenios.convenios.repository.ConvenioRepository;
import com.ucc.convenios.convenios.repository.ConvenioStatusHistoryRepository;
import com.ucc.convenios.documents.storage.LocalFileStorageService;
import com.ucc.convenios.notifications.service.ConvenioNotificationService;
import com.ucc.convenios.notifications.service.ReviewAlertService;
import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.shared.enums.CompanyDocumentRequestStatus;
import com.ucc.convenios.shared.enums.CompanyExternalDocumentType;
import com.ucc.convenios.shared.enums.CompanySubmittedDocumentStatus;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.shared.enums.ReviewAlertAudience;
import com.ucc.convenios.shared.enums.ReviewAlertType;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CompanyDocumentWorkflowService {

    private static final int TOKEN_EXPIRATION_DAYS = 15;
    private static final int MAX_EARLY_CORRECTION_ROUNDS = 6;
    private static final long MAX_FILE_SIZE_BYTES = 25L * 1024L * 1024L;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf",
            ".jpg",
            ".jpeg",
            ".png"
    );

    private static final List<CompanyExternalDocumentType> REQUIRED_DOCUMENT_TYPES = List.of(
            CompanyExternalDocumentType.CEDULA_REPRESENTANTE,
            CompanyExternalDocumentType.RUT_O_RUNT,
            CompanyExternalDocumentType.DOCUMENTO_ADICIONAL_1,
            CompanyExternalDocumentType.DOCUMENTO_ADICIONAL_2,
            CompanyExternalDocumentType.DOCUMENTO_ADICIONAL_3
    );

    private static final Map<CompanyExternalDocumentType, String> DEFAULT_DOCUMENT_NAMES = buildDefaultDocumentNames();

    private final CompanyDocumentRequestRepository documentRequestRepository;
    private final CompanyUploadTokenRepository uploadTokenRepository;
    private final CompanySubmittedDocumentRepository submittedDocumentRepository;
    private final ConvenioRepository convenioRepository;
    private final ConvenioStatusHistoryRepository convenioStatusHistoryRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final LocalFileStorageService localFileStorageService;
    private final ConvenioNotificationService convenioNotificationService;
    private final ReviewAlertService reviewAlertService;
    private final SecureRandom secureRandom = new SecureRandom();

    public CompanyDocumentWorkflowService(
            CompanyDocumentRequestRepository documentRequestRepository,
            CompanyUploadTokenRepository uploadTokenRepository,
            CompanySubmittedDocumentRepository submittedDocumentRepository,
            ConvenioRepository convenioRepository,
            ConvenioStatusHistoryRepository convenioStatusHistoryRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            LocalFileStorageService localFileStorageService,
            ConvenioNotificationService convenioNotificationService,
            ReviewAlertService reviewAlertService
    ) {
        this.documentRequestRepository = documentRequestRepository;
        this.uploadTokenRepository = uploadTokenRepository;
        this.submittedDocumentRepository = submittedDocumentRepository;
        this.convenioRepository = convenioRepository;
        this.convenioStatusHistoryRepository = convenioStatusHistoryRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.localFileStorageService = localFileStorageService;
        this.convenioNotificationService = convenioNotificationService;
        this.reviewAlertService = reviewAlertService;
    }

    @Transactional
    public CompanyDocumentRequestResponse requestCompanyDocuments(UUID convenioId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);

        validateResponsibleOrAdmin(convenio, currentUser);
        validateCompanyHasContactEmail(convenio);

        CompanyDocumentRequest lastRequest = findLastRequest(convenio);
        int nextRoundNumber = lastRequest == null ? 1 : lastRequest.getRoundNumber() + 1;

        if (nextRoundNumber > MAX_EARLY_CORRECTION_ROUNDS) {
            notifyEarlyCorrectionLimit(convenio);
            throw new BadRequestException("El convenio ya alcanzó el máximo de 6 correcciones tempranas");
        }

        revokeActiveTokens(lastRequest);

        CompanyDocumentRequest request = new CompanyDocumentRequest();
        request.setConvenio(convenio);
        request.setCompany(convenio.getCompany());
        request.setRoundNumber(nextRoundNumber);
        request.setStatus(CompanyDocumentRequestStatus.PENDIENTE_EMPRESA);

        CompanyDocumentRequest savedRequest = documentRequestRepository.save(request);

        String rawToken = generateRawToken();

        CompanyUploadToken uploadToken = new CompanyUploadToken();
        uploadToken.setRequest(savedRequest);
        uploadToken.setConvenio(convenio);
        uploadToken.setCompany(convenio.getCompany());
        uploadToken.setTokenHash(hashToken(rawToken));
        uploadToken.setRecipientEmail(convenio.getCompany().getContactEmail());
        uploadToken.setExpiresAt(LocalDateTime.now().plusDays(TOKEN_EXPIRATION_DAYS));
        uploadToken.setCreatedBy(currentUser);

        uploadTokenRepository.save(uploadToken);

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        convenio.setCurrentStatus(ConvenioStatus.PENDIENTE_DOCUMENTOS_EMPRESA);
        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.PENDIENTE_DOCUMENTOS_EMPRESA,
                "Solicitud documental enviada a la empresa. Ronda temprana " + nextRoundNumber,
                currentUser
        );

        convenioNotificationService.notifyCompanyDocumentRequest(
                savedConvenio,
                uploadToken,
                rawToken,
                REQUIRED_DOCUMENT_TYPES.stream()
                        .map(DEFAULT_DOCUMENT_NAMES::get)
                        .toList()
        );

        return CompanyDocumentRequestResponse.fromEntity(savedRequest);
    }

    @Transactional
    public PublicCompanyUploadInfoResponse getPublicUploadInfo(String rawToken) {
        CompanyUploadToken token = getValidToken(rawToken);
        return buildPublicUploadInfo(token);
    }

    @Transactional
    public CompanySubmittedDocumentResponse uploadCompanyDocument(
            String rawToken,
            CompanyExternalDocumentType documentType,
            String displayName,
            MultipartFile file
    ) {
        CompanyUploadToken token = getValidToken(rawToken);
        CompanyDocumentRequest request = token.getRequest();
        Convenio convenio = token.getConvenio();

        validateUploadedFile(file);
        validateCompanyDocumentType(documentType);

        CompanySubmittedDocument previousActiveDocument = findPreviousActiveDocument(convenio, documentType);

        Path storagePath;
        try {
            storagePath = localFileStorageService.saveCompanySubmittedDocument(
                    convenio.getId(),
                    request.getId(),
                    file.getOriginalFilename(),
                    file.getBytes()
            );
        } catch (Exception exception) {
            throw new BadRequestException("No se pudo guardar el documento enviado por la empresa");
        }

        CompanySubmittedDocument document = new CompanySubmittedDocument();
        document.setRequest(request);
        document.setConvenio(convenio);
        document.setDocumentType(documentType);
        document.setDisplayName(resolveDisplayName(documentType, displayName));
        document.setOriginalFilename(resolveOriginalFilename(file.getOriginalFilename()));
        document.setMimeType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStoragePath(storagePath.toString());
        document.setStatus(CompanySubmittedDocumentStatus.SUBIDO);

        CompanySubmittedDocument savedDocument = submittedDocumentRepository.save(document);

        if (previousActiveDocument != null) {
            replacePreviousDocument(previousActiveDocument, savedDocument);
        }

        request.setStatus(CompanyDocumentRequestStatus.DOCUMENTOS_RECIBIDOS);
        request.setSubmittedAt(LocalDateTime.now());
        documentRequestRepository.save(request);

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        convenio.setCurrentStatus(ConvenioStatus.DOCUMENTOS_EMPRESA_RECIBIDOS);
        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.DOCUMENTOS_EMPRESA_RECIBIDOS,
                "La empresa cargó documento externo: " + savedDocument.getDisplayName(),
                token.getCreatedBy()
        );

        notifyResponsibleCompanyUploadedDocuments(savedConvenio);

        return CompanySubmittedDocumentResponse.fromEntity(savedDocument);
    }

    @Transactional
    public CompanySubmittedDocumentResponse adminUploadCompanyDocument(
            UUID convenioId,
            CompanyExternalDocumentType documentType,
            String displayName,
            MultipartFile file,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        validateAdmin(currentUser);

        Convenio convenio = getConvenioWithDetails(convenioId);
        validateUploadedFile(file);
        validateCompanyDocumentType(documentType);

        CompanyDocumentRequest request = findOrCreateAdminDocumentRequest(convenio, currentUser);
        CompanySubmittedDocument previousActiveDocument = findPreviousActiveDocument(convenio, documentType);

        Path storagePath;
        try {
            storagePath = localFileStorageService.saveCompanySubmittedDocument(
                    convenio.getId(),
                    request.getId(),
                    file.getOriginalFilename(),
                    file.getBytes()
            );
        } catch (Exception exception) {
            throw new BadRequestException("No se pudo guardar el documento cargado por ADMIN");
        }

        CompanySubmittedDocument document = new CompanySubmittedDocument();
        document.setRequest(request);
        document.setConvenio(convenio);
        document.setDocumentType(documentType);
        document.setDisplayName(resolveDisplayName(documentType, displayName));
        document.setOriginalFilename(resolveOriginalFilename(file.getOriginalFilename()));
        document.setMimeType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStoragePath(storagePath.toString());
        document.setStatus(CompanySubmittedDocumentStatus.SUBIDO);
        document.setReviewComment("Documento cargado manualmente por ADMIN: " + currentUser.getEmail());

        CompanySubmittedDocument savedDocument = submittedDocumentRepository.save(document);

        if (previousActiveDocument != null) {
            replacePreviousDocument(previousActiveDocument, savedDocument);
        }

        request.setStatus(CompanyDocumentRequestStatus.DOCUMENTOS_RECIBIDOS);
        request.setSubmittedAt(LocalDateTime.now());
        documentRequestRepository.save(request);

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        if (shouldMoveToDocumentsReceived(previousStatus)) {
            convenio.setCurrentStatus(ConvenioStatus.DOCUMENTOS_EMPRESA_RECIBIDOS);
        }
        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                savedConvenio.getCurrentStatus(),
                "Documento externo cargado manualmente por ADMIN: " + savedDocument.getDisplayName(),
                currentUser
        );

        return CompanySubmittedDocumentResponse.fromEntity(savedDocument);
    }

    @Transactional(readOnly = true)
    public List<CompanyDocumentRequestResponse> findRequests(UUID convenioId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);
        validateResponsibleOrAdmin(convenio, currentUser);

        return documentRequestRepository.findByConvenioOrderByRoundNumberDesc(convenio)
                .stream()
                .map(CompanyDocumentRequestResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompanySubmittedDocumentResponse> findDocuments(UUID convenioId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);
        validateResponsibleOrAdmin(convenio, currentUser);

        return submittedDocumentRepository.findByConvenioOrderByUploadedAtDesc(convenio)
                .stream()
                .map(CompanySubmittedDocumentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public CompanySubmittedDocumentResponse approveDocument(
            UUID convenioId,
            UUID documentId,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);
        validateResponsibleOrAdmin(convenio, currentUser);

        CompanySubmittedDocument document = getSubmittedDocument(documentId);
        validateDocumentBelongsToConvenio(document, convenio);

        if (document.getStatus() == CompanySubmittedDocumentStatus.ELIMINADO ||
                document.getStatus() == CompanySubmittedDocumentStatus.REEMPLAZADO) {
            throw new BadRequestException("No se puede aprobar un documento eliminado o reemplazado");
        }

        document.setStatus(CompanySubmittedDocumentStatus.APROBADO);
        document.setApprovedAt(LocalDateTime.now());
        document.setReviewedAt(LocalDateTime.now());
        document.setReviewedBy(currentUser);
        document.setReviewComment("Documento aprobado por el responsable del convenio");
        document.setDeletionReason(null);

        CompanySubmittedDocument savedDocument = submittedDocumentRepository.save(document);

        return CompanySubmittedDocumentResponse.fromEntity(savedDocument);
    }

    @Transactional
    public CompanySubmittedDocumentResponse observeDocument(
            UUID convenioId,
            UUID documentId,
            CompanyDocumentReviewRequest request,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);
        validateResponsibleOrAdmin(convenio, currentUser);

        CompanySubmittedDocument document = getSubmittedDocument(documentId);
        validateDocumentBelongsToConvenio(document, convenio);

        if (document.getStatus() == CompanySubmittedDocumentStatus.REEMPLAZADO ||
                document.getStatus() == CompanySubmittedDocumentStatus.ELIMINADO) {
            throw new BadRequestException("No se puede observar un documento reemplazado o eliminado");
        }

        document.setReviewComment(request.getComment().trim());
        document.setReviewedAt(LocalDateTime.now());
        document.setReviewedBy(currentUser);
        document.setApprovedAt(null);

        if (request.isDeletePhysicalFile()) {
            localFileStorageService.deleteFileIfExists(document.getStoragePath());
            document.setDeletedFromStorageAt(LocalDateTime.now());
            document.setDeletionReason("Archivo observado y eliminado físicamente: " + request.getComment().trim());
            document.setStatus(CompanySubmittedDocumentStatus.ELIMINADO);
        } else {
            document.setStatus(CompanySubmittedDocumentStatus.OBSERVADO);
            document.setDeletionReason(null);
        }

        CompanySubmittedDocument savedDocument = submittedDocumentRepository.save(document);

        CompanyDocumentRequest lastRequest = findLastRequest(convenio);
        if (lastRequest != null) {
            lastRequest.setStatus(CompanyDocumentRequestStatus.OBSERVADA);
            lastRequest.setReviewedAt(LocalDateTime.now());
            lastRequest.setReviewedBy(currentUser);
            lastRequest.setReviewComment(request.getComment().trim());
            documentRequestRepository.save(lastRequest);
        }

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        convenio.setCurrentStatus(ConvenioStatus.DOCUMENTOS_OBSERVADOS_EMPRESA);
        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.DOCUMENTOS_OBSERVADOS_EMPRESA,
                "Documento externo observado: " + request.getComment().trim(),
                currentUser
        );

        return CompanySubmittedDocumentResponse.fromEntity(savedDocument);
    }

    @Transactional
    public CompanyDocumentRequestResponse requestCorrection(
            UUID convenioId,
            CompanyDocumentCorrectionRequest correctionRequest,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);
        validateResponsibleOrAdmin(convenio, currentUser);

        CompanyDocumentRequest lastRequest = findLastRequest(convenio);
        if (lastRequest == null) {
            throw new BadRequestException("No existe una solicitud documental previa para devolver corrección");
        }

        if (lastRequest.getRoundNumber() >= MAX_EARLY_CORRECTION_ROUNDS) {
            notifyEarlyCorrectionLimit(convenio);
            throw new BadRequestException("Este convenio ya llegó a 6 correcciones tempranas. El responsable debe decidir última corrección, radicar o descartar");
        }

        lastRequest.setStatus(CompanyDocumentRequestStatus.OBSERVADA);
        lastRequest.setReviewedAt(LocalDateTime.now());
        lastRequest.setReviewedBy(currentUser);
        lastRequest.setReviewComment(correctionRequest.getComment().trim());
        documentRequestRepository.save(lastRequest);

        return requestCompanyDocuments(convenioId, authentication);
    }

    @Transactional
    public CompanyDocumentRequestResponse markDocumentsApproved(UUID convenioId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);
        validateResponsibleOrAdmin(convenio, currentUser);

        CompanyDocumentRequest lastRequest = findLastRequest(convenio);
        if (lastRequest == null) {
            throw new BadRequestException("No existe solicitud documental para este convenio");
        }

        validateAllRequiredDocumentsApproved(convenio);

        lastRequest.setStatus(CompanyDocumentRequestStatus.APROBADA);
        lastRequest.setReviewedAt(LocalDateTime.now());
        lastRequest.setReviewedBy(currentUser);
        lastRequest.setReviewComment("Documentos externos aprobados por el responsable del convenio");

        CompanyDocumentRequest savedRequest = documentRequestRepository.save(lastRequest);

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        convenio.setCurrentStatus(ConvenioStatus.LISTO_PARA_RADICAR);
        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.LISTO_PARA_RADICAR,
                "Documentos externos aprobados. Convenio listo para radicar",
                currentUser
        );

        return CompanyDocumentRequestResponse.fromEntity(savedRequest);
    }


    @Transactional
    public CompanyDocumentRequestResponse discardEarlyDocumentProcess(
            UUID convenioId,
            CompanyDocumentDiscardRequest discardRequest,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        Convenio convenio = getConvenioWithDetails(convenioId);
        validateResponsibleOrAdmin(convenio, currentUser);

        CompanyDocumentRequest lastRequest = findLastRequest(convenio);
        if (lastRequest == null) {
            throw new BadRequestException("No existe solicitud documental para descartar este proceso");
        }

        String comment = discardRequest.getComment().trim();

        revokeActiveTokens(lastRequest);

        lastRequest.setStatus(CompanyDocumentRequestStatus.CANCELADA);
        lastRequest.setReviewedAt(LocalDateTime.now());
        lastRequest.setReviewedBy(currentUser);
        lastRequest.setReviewComment(comment);
        CompanyDocumentRequest savedRequest = documentRequestRepository.save(lastRequest);

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        convenio.setCurrentStatus(ConvenioStatus.DESISTIDO);
        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.DESISTIDO,
                "Proceso documental temprano descartado: " + comment,
                currentUser
        );

        convenioNotificationService.notifyEarlyDocumentProcessDiscarded(savedConvenio, comment);

        return CompanyDocumentRequestResponse.fromEntity(savedRequest);
    }

    @Transactional(readOnly = true)
    public boolean hasApprovedCompanyDocuments(Convenio convenio) {
        return REQUIRED_DOCUMENT_TYPES.stream()
                .allMatch(type -> submittedDocumentRepository
                        .findTopByConvenioAndDocumentTypeAndStatusInOrderByUploadedAtDesc(
                                convenio,
                                type,
                                List.of(CompanySubmittedDocumentStatus.APROBADO)
                        )
                        .isPresent());
    }

    private CompanyDocumentRequest findOrCreateAdminDocumentRequest(Convenio convenio, User currentUser) {
        CompanyDocumentRequest lastRequest = findLastRequest(convenio);

        if (lastRequest != null &&
                lastRequest.getStatus() != CompanyDocumentRequestStatus.APROBADA &&
                lastRequest.getStatus() != CompanyDocumentRequestStatus.CANCELADA &&
                lastRequest.getStatus() != CompanyDocumentRequestStatus.VENCIDA) {
            return lastRequest;
        }

        int nextRoundNumber = lastRequest == null ? 1 : lastRequest.getRoundNumber() + 1;

        CompanyDocumentRequest request = new CompanyDocumentRequest();
        request.setConvenio(convenio);
        request.setCompany(convenio.getCompany());
        request.setRoundNumber(nextRoundNumber);
        request.setStatus(CompanyDocumentRequestStatus.DOCUMENTOS_RECIBIDOS);
        request.setSubmittedAt(LocalDateTime.now());
        request.setReviewedBy(currentUser);
        request.setReviewComment("Solicitud documental creada por carga manual de ADMIN");

        return documentRequestRepository.save(request);
    }

    private boolean shouldMoveToDocumentsReceived(ConvenioStatus currentStatus) {
        return currentStatus == ConvenioStatus.BORRADOR ||
                currentStatus == ConvenioStatus.PENDIENTE_DOCUMENTOS_EMPRESA ||
                currentStatus == ConvenioStatus.DOCUMENTOS_OBSERVADOS_EMPRESA ||
                currentStatus == ConvenioStatus.DOCUMENTOS_EMPRESA_RECIBIDOS;
    }

    private void validateAllRequiredDocumentsApproved(Convenio convenio) {
        List<String> missingDocuments = REQUIRED_DOCUMENT_TYPES.stream()
                .filter(type -> submittedDocumentRepository
                        .findTopByConvenioAndDocumentTypeAndStatusInOrderByUploadedAtDesc(
                                convenio,
                                type,
                                List.of(CompanySubmittedDocumentStatus.APROBADO)
                        )
                        .isEmpty())
                .map(type -> DEFAULT_DOCUMENT_NAMES.get(type))
                .toList();

        if (!missingDocuments.isEmpty()) {
            throw new BadRequestException("Faltan documentos aprobados: " + String.join(", ", missingDocuments));
        }
    }

    private CompanySubmittedDocument findPreviousActiveDocument(Convenio convenio, CompanyExternalDocumentType documentType) {
        return submittedDocumentRepository.findTopByConvenioAndDocumentTypeAndStatusInOrderByUploadedAtDesc(
                        convenio,
                        documentType,
                        List.of(
                                CompanySubmittedDocumentStatus.SUBIDO,
                                CompanySubmittedDocumentStatus.OBSERVADO,
                                CompanySubmittedDocumentStatus.APROBADO
                        )
                )
                .orElse(null);
    }

    private void replacePreviousDocument(
            CompanySubmittedDocument previousDocument,
            CompanySubmittedDocument replacementDocument
    ) {
        localFileStorageService.deleteFileIfExists(previousDocument.getStoragePath());
        previousDocument.setDeletedFromStorageAt(LocalDateTime.now());
        previousDocument.setReplacedByDocument(replacementDocument);
        previousDocument.setStatus(CompanySubmittedDocumentStatus.REEMPLAZADO);
        previousDocument.setReviewComment("Archivo reemplazado por una nueva carga de la empresa");
        previousDocument.setDeletionReason("Archivo físico eliminado por reemplazo documental");
        submittedDocumentRepository.save(previousDocument);
    }

    private PublicCompanyUploadInfoResponse buildPublicUploadInfo(CompanyUploadToken token) {
        CompanyDocumentRequest request = token.getRequest();
        Convenio convenio = token.getConvenio();

        PublicCompanyUploadInfoResponse response = new PublicCompanyUploadInfoResponse();
        response.setRequestId(request.getId());
        response.setConvenioId(convenio.getId());
        response.setConvenioCode(convenio.getCode());
        response.setCompanyName(token.getCompany().getBusinessName());
        response.setStatus(request.getStatus().name());
        response.setRoundNumber(request.getRoundNumber());
        response.setExpiresAt(token.getExpiresAt());
        response.setRequiredDocuments(REQUIRED_DOCUMENT_TYPES.stream()
                .map(type -> new RequiredCompanyDocumentResponse(type.name(), DEFAULT_DOCUMENT_NAMES.get(type)))
                .toList());

        return response;
    }

    private CompanyUploadToken getValidToken(String rawToken) {
        CompanyUploadToken token = uploadTokenRepository.findByTokenHashAndRevokedAtIsNull(hashToken(rawToken))
                .orElseThrow(() -> new ResourceNotFoundException("Enlace de carga documental no encontrado o revocado"));

        if (token.isExpired()) {
            token.getRequest().setStatus(CompanyDocumentRequestStatus.VENCIDA);
            documentRequestRepository.save(token.getRequest());
            throw new BadRequestException("El enlace de carga documental venció");
        }

        if (token.isRevoked()) {
            throw new BadRequestException("El enlace de carga documental fue revocado");
        }

        return token;
    }

    private void revokeActiveTokens(CompanyDocumentRequest request) {
        if (request == null) {
            return;
        }

        uploadTokenRepository.findByRequestAndRevokedAtIsNull(request)
                .forEach(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    uploadTokenRepository.save(token);
                });
    }

    private void notifyResponsibleCompanyUploadedDocuments(Convenio convenio) {
        User responsible = convenio.getCreatedBy();

        reviewAlertService.createAlert(
                null,
                convenio,
                responsible,
                ReviewAlertType.DOCUMENTOS_EMPRESA_RECIBIDOS,
                ReviewAlertAudience.SOLICITANTE,
                "Documentos de empresa recibidos",
                "La empresa cargó documentos externos para el convenio " + convenio.getCode() + ". Revisa si están correctos."
        );

        convenioNotificationService.notifyResponsibleCompanyUploadedDocuments(convenio);
    }

    private void notifyEarlyCorrectionLimit(Convenio convenio) {
        User responsible = convenio.getCreatedBy();

        reviewAlertService.createAlert(
                null,
                convenio,
                responsible,
                ReviewAlertType.LIMITE_CORRECCIONES_TEMPRANAS,
                ReviewAlertAudience.SOLICITANTE,
                "Límite de correcciones tempranas alcanzado",
                "El convenio " + convenio.getCode() + " llegó a 6 correcciones tempranas. Decide si haces una última revisión, radicas o descartas."
        );

        convenioNotificationService.notifyEarlyCorrectionLimit(convenio);
    }

    private void validateAdmin(User user) {
        if (!hasRole(user, "ADMIN")) {
            throw new BadRequestException("Solo ADMIN puede cargar documentación manualmente");
        }
    }

    private boolean hasRole(User user, String roleName) {
        return userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .anyMatch(roleName::equals);
    }

    private void validateCompanyDocumentType(CompanyExternalDocumentType documentType) {
        if (!REQUIRED_DOCUMENT_TYPES.contains(documentType)) {
            throw new BadRequestException("Tipo documental no permitido");
        }
    }

    private void validateUploadedFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo es obligatorio");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("El archivo supera el tamaño máximo permitido de 25 MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("El archivo debe tener un nombre válido");
        }

        String normalizedFilename = originalFilename.trim().toLowerCase(Locale.ROOT);
        boolean hasAllowedExtension = ALLOWED_EXTENSIONS.stream().anyMatch(normalizedFilename::endsWith);
        if (!hasAllowedExtension) {
            throw new BadRequestException("Formato de archivo no permitido. Solo se aceptan PDF, JPG, JPEG o PNG");
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            throw new BadRequestException("No se pudo identificar el tipo del archivo");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Tipo de archivo no permitido. Solo se aceptan PDF, JPG, JPEG o PNG");
        }
    }

    private void validateCompanyHasContactEmail(Convenio convenio) {
        if (convenio.getCompany().getContactEmail() == null || convenio.getCompany().getContactEmail().isBlank()) {
            throw new BadRequestException("La empresa no tiene correo de contacto configurado");
        }
    }

    private void validateResponsibleOrAdmin(Convenio convenio, User user) {
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
            throw new BadRequestException("Solo el responsable del convenio, Proyección Social o ADMIN pueden ejecutar esta acción");
        }
    }

    private CompanyDocumentRequest findLastRequest(Convenio convenio) {
        return documentRequestRepository.findTopByConvenioOrderByRoundNumberDesc(convenio).orElse(null);
    }

    private CompanySubmittedDocument getSubmittedDocument(UUID documentId) {
        return submittedDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento externo no encontrado"));
    }

    private void validateDocumentBelongsToConvenio(CompanySubmittedDocument document, Convenio convenio) {
        if (!document.getConvenio().getId().equals(convenio.getId())) {
            throw new BadRequestException("El documento no pertenece a este convenio");
        }
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

    private void registerStatusHistory(
            Convenio convenio,
            ConvenioStatus previousStatus,
            ConvenioStatus newStatus,
            String comment,
            User performedBy
    ) {
        ConvenioStatusHistory history = new ConvenioStatusHistory();
        history.setConvenio(convenio);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setPreviousStage(convenio.getCurrentStage());
        history.setNewStage(convenio.getCurrentStage());
        history.setComment(comment);
        history.setPerformedBy(performedBy);

        convenioStatusHistoryRepository.save(history);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new BadRequestException("Token inválido");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes());
            StringBuilder hex = new StringBuilder();

            for (byte value : hash) {
                hex.append(String.format("%02x", value));
            }

            return hex.toString();
        } catch (Exception exception) {
            throw new BadRequestException("No se pudo procesar el token");
        }
    }

    private String resolveDisplayName(CompanyExternalDocumentType documentType, String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return DEFAULT_DOCUMENT_NAMES.get(documentType);
        }

        return displayName.trim();
    }

    private String resolveOriginalFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "documento";
        }

        return originalFilename.trim();
    }

    private static Map<CompanyExternalDocumentType, String> buildDefaultDocumentNames() {
        Map<CompanyExternalDocumentType, String> names = new EnumMap<>(CompanyExternalDocumentType.class);
        names.put(CompanyExternalDocumentType.CEDULA_REPRESENTANTE, "Cédula del representante legal");
        names.put(CompanyExternalDocumentType.RUT_O_RUNT, "RUT o RUNT actualizado");
        names.put(CompanyExternalDocumentType.DOCUMENTO_ADICIONAL_1, "Documento adicional 1");
        names.put(CompanyExternalDocumentType.DOCUMENTO_ADICIONAL_2, "Documento adicional 2");
        names.put(CompanyExternalDocumentType.DOCUMENTO_ADICIONAL_3, "Documento adicional 3");
        return names;
    }
}