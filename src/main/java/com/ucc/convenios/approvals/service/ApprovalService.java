package com.ucc.convenios.approvals.service;

import com.ucc.convenios.approvals.dto.ApprovalDecisionRequest;
import com.ucc.convenios.approvals.dto.ApprovalRoundResponse;
import com.ucc.convenios.approvals.dto.ApprovalStepResponse;
import com.ucc.convenios.approvals.entity.ApprovalRound;
import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.approvals.repository.ApprovalRoundRepository;
import com.ucc.convenios.approvals.repository.ApprovalStepRepository;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioGeneratedDocument;
import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.convenios.repository.ConvenioRepository;
import com.ucc.convenios.convenios.repository.ConvenioStatusHistoryRepository;
import com.ucc.convenios.convenios.repository.ConvenioVersionRepository;
import com.ucc.convenios.convenios.service.ConvenioDocumentService;
import com.ucc.convenios.notifications.service.ConvenioNotificationService;
import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.repository.RoleRepository;
import com.ucc.convenios.shared.enums.ApprovalRoundStatus;
import com.ucc.convenios.shared.enums.ApprovalStepStatus;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.shared.enums.ConvenioVersionStatus;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.ReviewerProfile;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.ReviewerProfileRepository;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
public class ApprovalService {

    private static final int MAX_REVISION_ISSUES = 3;

    private final ApprovalRoundRepository approvalRoundRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final ApprovalAssignmentService approvalAssignmentService;
    private final ConvenioRepository convenioRepository;
    private final ConvenioVersionRepository convenioVersionRepository;
    private final ConvenioStatusHistoryRepository convenioStatusHistoryRepository;
    private final UserRepository userRepository;
    private final ConvenioDocumentService convenioDocumentService;
    private final RoleRepository roleRepository;
    private final ReviewerProfileRepository reviewerProfileRepository;
    private final ConvenioNotificationService convenioNotificationService;

    public ApprovalService(
            ApprovalRoundRepository approvalRoundRepository,
            ApprovalStepRepository approvalStepRepository,
            ApprovalAssignmentService approvalAssignmentService,
            ConvenioRepository convenioRepository,
            ConvenioVersionRepository convenioVersionRepository,
            ConvenioStatusHistoryRepository convenioStatusHistoryRepository,
            UserRepository userRepository,
            ConvenioDocumentService convenioDocumentService,
            RoleRepository roleRepository,
            ReviewerProfileRepository reviewerProfileRepository,
            ConvenioNotificationService convenioNotificationService
    ) {
        this.approvalRoundRepository = approvalRoundRepository;
        this.approvalStepRepository = approvalStepRepository;
        this.approvalAssignmentService = approvalAssignmentService;
        this.convenioRepository = convenioRepository;
        this.convenioVersionRepository = convenioVersionRepository;
        this.convenioStatusHistoryRepository = convenioStatusHistoryRepository;
        this.userRepository = userRepository;
        this.convenioDocumentService = convenioDocumentService;
        this.roleRepository = roleRepository;
        this.reviewerProfileRepository = reviewerProfileRepository;
        this.convenioNotificationService = convenioNotificationService;
    }

    @Transactional
    public ApprovalRound createInitialApprovalRound(
            Convenio convenio,
            ConvenioVersion convenioVersion,
            ConvenioStage firstStage
    ) {
        int roundNumber = getNextRoundNumber(convenio);

        ApprovalRound round = new ApprovalRound();
        round.setConvenio(convenio);
        round.setConvenioVersion(convenioVersion);
        round.setRoundNumber(roundNumber);
        round.setStatus(ApprovalRoundStatus.EN_PROCESO);

        ApprovalRound savedRound = approvalRoundRepository.save(round);

        createStep(savedRound, firstStage);

        return savedRound;
    }

    @Transactional(readOnly = true)
    public List<ApprovalStepResponse> getMyPendingApprovals(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        return approvalStepRepository
                .findByAssignedUserAndStatusOrderByAssignedAtDesc(currentUser, ApprovalStepStatus.PENDIENTE)
                .stream()
                .map(ApprovalStepResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApprovalRoundResponse> getRoundsByConvenio(UUID convenioId) {
        Convenio convenio = getConvenioById(convenioId);

        return approvalRoundRepository.findByConvenioOrderByRoundNumberDesc(convenio)
                .stream()
                .map(ApprovalRoundResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApprovalStepResponse> getStepsByRound(UUID roundId) {
        ApprovalRound round = approvalRoundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Ronda de aprobación no encontrada"));

        return approvalStepRepository.findByApprovalRoundOrderByStageOrderAsc(round)
                .stream()
                .map(ApprovalStepResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ApprovalStepResponse approveStep(UUID stepId, ApprovalDecisionRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        ApprovalStep step = getStepWithDetails(stepId);

        validateStepCanBeAnswered(step, currentUser);

        step.setStatus(ApprovalStepStatus.APROBADO);
        step.setDecisionComment(normalizeComment(request.getComment(), "Aprobado"));
        step.setRespondedAt(LocalDateTime.now());
        step.setApprovalCode(generateApprovalCode(step));
        step.setSealText(generateSealText(step, currentUser));

        ApprovalStep savedStep = approvalStepRepository.save(step);

        approvalAssignmentService.releaseReviewerCase(step.getAssignedUser(), step.getStage());

        advanceToNextStageOrFinish(step.getApprovalRound(), currentUser);

        return ApprovalStepResponse.fromEntity(savedStep);
    }

    @Transactional
    public ApprovalStepResponse requestCorrection(UUID stepId, ApprovalDecisionRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        ApprovalStep step = getStepWithDetails(stepId);

        validateStepCanBeAnswered(step, currentUser);

        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new BadRequestException("El comentario es obligatorio para solicitar corrección");
        }

        step.setStatus(ApprovalStepStatus.CORRECCION_SOLICITADA);
        step.setDecisionComment(request.getComment().trim());
        step.setRespondedAt(LocalDateTime.now());

        ApprovalStep savedStep = approvalStepRepository.save(step);

        convenioDocumentService.createCorrectionDocument(
                step.getApprovalRound().getConvenio(),
                step.getApprovalRound().getConvenioVersion(),
                savedStep,
                currentUser,
                request.getComment().trim()
        );

        approvalAssignmentService.releaseReviewerCase(step.getAssignedUser(), step.getStage());

        moveConvenioToCorrection(step.getApprovalRound(), currentUser, request.getComment().trim());

        return ApprovalStepResponse.fromEntity(savedStep);
    }

    @Transactional
    public ApprovalStepResponse rejectStep(UUID stepId, ApprovalDecisionRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        ApprovalStep step = getStepWithDetails(stepId);

        validateStepCanBeAnswered(step, currentUser);

        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new BadRequestException("El comentario es obligatorio para rechazar");
        }

        step.setStatus(ApprovalStepStatus.RECHAZADO);
        step.setDecisionComment(request.getComment().trim());
        step.setRespondedAt(LocalDateTime.now());

        ApprovalStep savedStep = approvalStepRepository.save(step);

        convenioDocumentService.createRejectedDocument(
                step.getApprovalRound().getConvenio(),
                step.getApprovalRound().getConvenioVersion(),
                savedStep,
                currentUser,
                request.getComment().trim()
        );

        approvalAssignmentService.releaseReviewerCase(step.getAssignedUser(), step.getStage());

        rejectConvenio(step.getApprovalRound(), currentUser, request.getComment().trim());

        return ApprovalStepResponse.fromEntity(savedStep);
    }

    private void advanceToNextStageOrFinish(ApprovalRound round, User currentUser) {
        ConvenioStage currentStage = round.getConvenio().getCurrentStage();
        ConvenioStage nextStage = getNextStage(currentStage);

        if (nextStage == null) {
            finishApprovalRound(round, currentUser);
            return;
        }

        Convenio convenio = round.getConvenio();

        ConvenioStage previousStage = convenio.getCurrentStage();
        ConvenioStatus previousStatus = convenio.getCurrentStatus();

        convenio.setCurrentStatus(ConvenioStatus.EN_REVISION);
        convenio.setCurrentStage(nextStage);

        Convenio savedConvenio = convenioRepository.save(convenio);

        registerStatusHistory(
                savedConvenio,
                previousStatus,
                ConvenioStatus.EN_REVISION,
                previousStage,
                nextStage,
                "Convenio avanzado a etapa " + nextStage.name(),
                currentUser
        );

        createStep(round, nextStage);
    }

    private void finishApprovalRound(ApprovalRound round, User currentUser) {
        Convenio convenio = round.getConvenio();
        ConvenioVersion version = round.getConvenioVersion();

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        ConvenioStage previousStage = convenio.getCurrentStage();

        round.setStatus(ApprovalRoundStatus.APROBADA);
        round.setFinishedAt(LocalDateTime.now());
        approvalRoundRepository.save(round);

        convenio.setCurrentStatus(ConvenioStatus.APROBADO_PARA_FIRMA);
        convenio.setCurrentStage(null);
        convenioRepository.save(convenio);

        version.setStatus(ConvenioVersionStatus.FINAL);

        List<ApprovalStep> steps = approvalStepRepository.findByApprovalRoundOrderByStageOrderAsc(round);

        ConvenioGeneratedDocument finalDocument = convenioDocumentService.createFinalApprovedDocument(
                convenio,
                version,
                steps,
                currentUser
        );

        version.setGeneratedPdfStoragePath(finalDocument.getStoragePath());
        version.setGeneratedPdfUrl(finalDocument.getUrl());
        convenioVersionRepository.save(version);

        registerStatusHistory(
                convenio,
                previousStatus,
                ConvenioStatus.APROBADO_PARA_FIRMA,
                previousStage,
                null,
                "Convenio aprobado por todas las etapas. Documento final aprobado generado con constancias.",
                currentUser
        );

        convenioNotificationService.notifyProjectionFormalizationPending(convenio);
    }

    private void moveConvenioToCorrection(ApprovalRound round, User currentUser, String comment) {
        Convenio convenio = round.getConvenio();

        int newIssueCount = convenio.getRevisionIssueCount() + 1;
        convenio.setRevisionIssueCount(newIssueCount);

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        ConvenioStage previousStage = convenio.getCurrentStage();

        round.setFinishedAt(LocalDateTime.now());

        if (newIssueCount >= MAX_REVISION_ISSUES) {
            round.setStatus(ApprovalRoundStatus.RECHAZADA);
            approvalRoundRepository.save(round);

            convenio.setCurrentStatus(ConvenioStatus.RECHAZADO);
            convenio.setCurrentStage(null);
            convenioRepository.save(convenio);

            registerStatusHistory(
                    convenio,
                    previousStatus,
                    ConvenioStatus.RECHAZADO,
                    previousStage,
                    null,
                    "Convenio rechazado automáticamente por alcanzar el límite de 3 incidencias de revisión.",
                    currentUser
            );

            return;
        }

        round.setStatus(ApprovalRoundStatus.CANCELADA_POR_NUEVA_VERSION);
        approvalRoundRepository.save(round);

        convenio.setCurrentStatus(ConvenioStatus.EN_CORRECCION);
        convenio.setCurrentStage(null);
        convenioRepository.save(convenio);

        registerStatusHistory(
                convenio,
                previousStatus,
                ConvenioStatus.EN_CORRECCION,
                previousStage,
                null,
                "Corrección solicitada: " + comment,
                currentUser
        );
    }

    private void rejectConvenio(ApprovalRound round, User currentUser, String comment) {
        Convenio convenio = round.getConvenio();

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        ConvenioStage previousStage = convenio.getCurrentStage();

        round.setStatus(ApprovalRoundStatus.RECHAZADA);
        round.setFinishedAt(LocalDateTime.now());
        approvalRoundRepository.save(round);

        convenio.setCurrentStatus(ConvenioStatus.RECHAZADO);
        convenio.setCurrentStage(null);
        convenioRepository.save(convenio);

        registerStatusHistory(
                convenio,
                previousStatus,
                ConvenioStatus.RECHAZADO,
                previousStage,
                null,
                "Convenio rechazado: " + comment,
                currentUser
        );
    }

    private ApprovalStep createStep(ApprovalRound round, ConvenioStage stage) {
        if (approvalStepRepository.existsByApprovalRoundAndStage(round, stage)) {
            throw new BadRequestException("Ya existe una etapa de aprobación para " + stage.name());
        }

        User assignedUser = approvalAssignmentService.assignReviewerForStage(
                stage,
                round.getConvenio().getConvenioType()
        );

        ApprovalStep step = new ApprovalStep();
        step.setApprovalRound(round);
        step.setStage(stage);
        step.setStageOrder(getStageOrder(stage));
        step.setAssignedUser(assignedUser);
        step.setStatus(ApprovalStepStatus.PENDIENTE);
        step.setAssignedAt(LocalDateTime.now());
        step.setDueAt(LocalDateTime.now().plusDays(7));

        ApprovalStep savedStep = approvalStepRepository.save(step);

        convenioNotificationService.notifyReviewerAssigned(savedStep);

        return savedStep;
    }

    private void validateStepCanBeAnswered(ApprovalStep step, User currentUser) {
        if (step.getStatus() != ApprovalStepStatus.PENDIENTE) {
            throw new BadRequestException("Esta etapa ya fue respondida");
        }

        if (!step.getAssignedUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Solo el usuario asignado puede responder esta etapa");
        }

        if (step.getApprovalRound().getStatus() != ApprovalRoundStatus.EN_PROCESO) {
            throw new BadRequestException("La ronda de aprobación ya no está activa");
        }
    }

    private int getNextRoundNumber(Convenio convenio) {
        return approvalRoundRepository.findFirstByConvenioOrderByRoundNumberDesc(convenio)
                .map(round -> round.getRoundNumber() + 1)
                .orElse(1);
    }

    private ConvenioStage getNextStage(ConvenioStage currentStage) {
        if (currentStage == null) {
            return ConvenioStage.PROYECCION;
        }

        return switch (currentStage) {
            case PROYECCION -> ConvenioStage.JURIDICA;
            case JURIDICA -> ConvenioStage.RECTORIA;
            case RECTORIA -> null;
        };
    }

    private int getStageOrder(ConvenioStage stage) {
        return switch (stage) {
            case PROYECCION -> 1;
            case JURIDICA -> 2;
            case RECTORIA -> 3;
        };
    }

    private String generateApprovalCode(ApprovalStep step) {
        String year = String.valueOf(Year.now().getValue());

        return "APR-"
                + step.getStage().name()
                + "-"
                + year
                + "-"
                + step.getId().toString().substring(0, 8).toUpperCase();
    }

    private String generateSealText(ApprovalStep step, User user) {
        String sealName = getReviewerSealName(user, step.getStage());

        return "APROBADO\n"
                + "Etapa: " + step.getStage().name() + "\n"
                + "Responsable: " + sealName + "\n"
                + "Correo: " + user.getEmail() + "\n"
                + "Fecha: " + LocalDateTime.now() + "\n"
                + "Código: " + step.getApprovalCode();
    }

    private String getReviewerSealName(User user, ConvenioStage stage) {
        List<String> roleNames = getCandidateRoleNamesForStage(stage);

        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName).orElse(null);

            if (role == null) {
                continue;
            }

            String sealName = reviewerProfileRepository.findByUserAndRole(user, role)
                    .map(ReviewerProfile::getSealName)
                    .filter(value -> value != null && !value.isBlank())
                    .orElse(null);

            if (sealName != null) {
                return sealName;
            }
        }

        return generateDefaultSealName(user.getFullName());
    }

    private List<String> getCandidateRoleNamesForStage(ConvenioStage stage) {
        return switch (stage) {
            case PROYECCION -> List.of("GESTOR_PROYECCION");
            case JURIDICA -> List.of("REVISOR_JURIDICO");
            case RECTORIA -> List.of("RECTORIA", "RECTOR_MEDELLIN");
        };
    }

    private String generateDefaultSealName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "Revisor";
        }

        String[] parts = fullName.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0];
        }

        String firstInitial = parts[0].substring(0, 1).toUpperCase();
        String lastName = parts[parts.length - 1];

        return firstInitial + ". " + capitalize(lastName);
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        String lower = value.toLowerCase();

        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }

    private ApprovalStep getStepWithDetails(UUID stepId) {
        return approvalStepRepository.findWithDetailsById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Etapa de aprobación no encontrada"));
    }

    private Convenio getConvenioById(UUID convenioId) {
        return convenioRepository.findById(convenioId)
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

    private String normalizeComment(String comment, String defaultComment) {
        if (comment == null || comment.isBlank()) {
            return defaultComment;
        }

        return comment.trim();
    }
}