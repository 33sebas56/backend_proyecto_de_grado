package com.ucc.convenios.approvals.service;

import com.ucc.convenios.approvals.entity.ApprovalRound;
import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.approvals.repository.ApprovalRoundRepository;
import com.ucc.convenios.approvals.repository.ApprovalStepRepository;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.convenios.repository.ConvenioRepository;
import com.ucc.convenios.convenios.repository.ConvenioStatusHistoryRepository;
import com.ucc.convenios.convenios.service.ConvenioDocumentService;
import com.ucc.convenios.notifications.service.ReviewAlertService;
import com.ucc.convenios.shared.enums.ApprovalRoundStatus;
import com.ucc.convenios.shared.enums.ApprovalStepStatus;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.shared.enums.ReviewAlertAudience;
import com.ucc.convenios.shared.enums.ReviewAlertType;
import com.ucc.convenios.users.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalDeadlineService {

    private static final int MAX_REVISION_ISSUES = 3;

    private final ApprovalStepRepository approvalStepRepository;
    private final ApprovalRoundRepository approvalRoundRepository;
    private final ConvenioRepository convenioRepository;
    private final ConvenioStatusHistoryRepository convenioStatusHistoryRepository;
    private final ConvenioDocumentService convenioDocumentService;
    private final ApprovalAssignmentService approvalAssignmentService;
    private final ReviewAlertService reviewAlertService;

    public ApprovalDeadlineService(
            ApprovalStepRepository approvalStepRepository,
            ApprovalRoundRepository approvalRoundRepository,
            ConvenioRepository convenioRepository,
            ConvenioStatusHistoryRepository convenioStatusHistoryRepository,
            ConvenioDocumentService convenioDocumentService,
            ApprovalAssignmentService approvalAssignmentService,
            ReviewAlertService reviewAlertService
    ) {
        this.approvalStepRepository = approvalStepRepository;
        this.approvalRoundRepository = approvalRoundRepository;
        this.convenioRepository = convenioRepository;
        this.convenioStatusHistoryRepository = convenioStatusHistoryRepository;
        this.convenioDocumentService = convenioDocumentService;
        this.approvalAssignmentService = approvalAssignmentService;
        this.reviewAlertService = reviewAlertService;
    }

    @Transactional
    public void processPendingDeadlines() {
        List<ApprovalStep> pendingSteps = approvalStepRepository.findByStatus(ApprovalStepStatus.PENDIENTE);
        LocalDateTime now = LocalDateTime.now();

        for (ApprovalStep step : pendingSteps) {
            processStepDeadline(step, now);
        }
    }

    private void processStepDeadline(ApprovalStep step, LocalDateTime now) {
        if (step.getDueAt() == null) {
            step.setDueAt(step.getAssignedAt().plusDays(7));
            approvalStepRepository.save(step);
        }

        LocalDateTime assignedAt = step.getAssignedAt();

        if (step.getFirstReminderSentAt() == null && !now.isBefore(assignedAt.plusDays(1))) {
            sendReminder(
                    step,
                    ReviewAlertType.PRIMER_RECORDATORIO,
                    "Primer recordatorio de revisión pendiente",
                    "Tiene una revisión pendiente para el convenio " + step.getApprovalRound().getConvenio().getCode()
                            + ". Han pasado 1 día desde la asignación."
            );
            step.setFirstReminderSentAt(now);
            approvalStepRepository.save(step);
        }

        if (step.getSecondReminderSentAt() == null && !now.isBefore(assignedAt.plusDays(4))) {
            sendReminder(
                    step,
                    ReviewAlertType.SEGUNDO_RECORDATORIO,
                    "Segundo recordatorio de revisión pendiente",
                    "Tiene una revisión pendiente para el convenio " + step.getApprovalRound().getConvenio().getCode()
                            + ". Han pasado 4 días desde la asignación."
            );
            step.setSecondReminderSentAt(now);
            approvalStepRepository.save(step);
        }

        if (step.getFinalReminderSentAt() == null && !now.isBefore(step.getDueAt())) {
            sendReminder(
                    step,
                    ReviewAlertType.ULTIMO_RECORDATORIO,
                    "Último recordatorio de revisión pendiente",
                    "La revisión del convenio " + step.getApprovalRound().getConvenio().getCode()
                            + " llega a su fecha límite."
            );
            step.setFinalReminderSentAt(now);
            approvalStepRepository.save(step);
        }

        if (!now.isBefore(step.getDueAt())) {
            expireStep(step, now);
        }
    }

    private void sendReminder(
            ApprovalStep step,
            ReviewAlertType alertType,
            String title,
            String message
    ) {
        reviewAlertService.createAlert(
                step,
                step.getApprovalRound().getConvenio(),
                step.getAssignedUser(),
                alertType,
                ReviewAlertAudience.REVISOR,
                title,
                message
        );
    }

    private void expireStep(ApprovalStep step, LocalDateTime now) {
        ApprovalRound round = step.getApprovalRound();
        Convenio convenio = round.getConvenio();
        ConvenioVersion version = round.getConvenioVersion();

        String comment = "La revisión de la etapa " + step.getStage().name()
                + " venció porque el responsable no respondió dentro del plazo de 7 días.";

        step.setStatus(ApprovalStepStatus.VENCIDO);
        step.setDecisionComment(comment);
        step.setExpiredAt(now);
        step.setRespondedAt(now);
        ApprovalStep savedStep = approvalStepRepository.save(step);

        convenioDocumentService.createExpiredReviewDocument(
                convenio,
                version,
                savedStep,
                step.getAssignedUser(),
                comment
        );

        approvalAssignmentService.releaseReviewerCase(step.getAssignedUser(), step.getStage());

        registerExpirationAlerts(savedStep, convenio, comment);

        int newIssueCount = convenio.getRevisionIssueCount() + 1;
        convenio.setRevisionIssueCount(newIssueCount);

        ConvenioStatus previousStatus = convenio.getCurrentStatus();
        ConvenioStage previousStage = convenio.getCurrentStage();

        round.setFinishedAt(now);

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
                    "Convenio rechazado automáticamente por alcanzar el límite de incidencias de revisión.",
                    step.getAssignedUser()
            );

            reviewAlertService.createAlert(
                    savedStep,
                    convenio,
                    convenio.getCreatedBy(),
                    ReviewAlertType.LIMITE_INCIDENCIAS_ALCANZADO,
                    ReviewAlertAudience.SOLICITANTE,
                    "Convenio rechazado por límite de incidencias",
                    "El convenio " + convenio.getCode()
                            + " alcanzó el máximo de 3 incidencias permitidas y fue rechazado automáticamente."
            );

            reviewAlertService.createAlert(
                    savedStep,
                    convenio,
                    null,
                    ReviewAlertType.LIMITE_INCIDENCIAS_ALCANZADO,
                    ReviewAlertAudience.ADMIN,
                    "Convenio rechazado por límite de incidencias",
                    "El convenio " + convenio.getCode()
                            + " alcanzó el máximo de 3 incidencias permitidas."
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
                "Revisión vencida. El convenio pasa a corrección/incidencia para nueva gestión.",
                step.getAssignedUser()
        );

        reviewAlertService.createAlert(
                savedStep,
                convenio,
                convenio.getCreatedBy(),
                ReviewAlertType.INCIDENCIA_REGISTRADA,
                ReviewAlertAudience.SOLICITANTE,
                "Incidencia registrada en el convenio",
                "El convenio " + convenio.getCode()
                        + " pasó a corrección/incidencia porque una revisión venció sin respuesta."
        );
    }

    private void registerExpirationAlerts(ApprovalStep step, Convenio convenio, String comment) {
        reviewAlertService.createAlert(
                step,
                convenio,
                step.getAssignedUser(),
                ReviewAlertType.REVISION_VENCIDA,
                ReviewAlertAudience.REVISOR,
                "Revisión vencida",
                comment
        );

        reviewAlertService.createAlert(
                step,
                convenio,
                convenio.getCreatedBy(),
                ReviewAlertType.REVISION_VENCIDA,
                ReviewAlertAudience.SOLICITANTE,
                "Una revisión de su convenio venció",
                "La etapa " + step.getStage().name()
                        + " del convenio " + convenio.getCode()
                        + " venció sin respuesta del responsable."
        );

        reviewAlertService.createAlert(
                step,
                convenio,
                null,
                ReviewAlertType.REVISION_VENCIDA,
                ReviewAlertAudience.ADMIN,
                "Revisión vencida sin respuesta",
                "La etapa " + step.getStage().name()
                        + " del convenio " + convenio.getCode()
                        + " venció sin respuesta."
        );

        reviewAlertService.createAlert(
                step,
                convenio,
                null,
                ReviewAlertType.REVISION_VENCIDA,
                ReviewAlertAudience.PROYECCION_SOCIAL,
                "Revisión vencida sin respuesta",
                "La etapa " + step.getStage().name()
                        + " del convenio " + convenio.getCode()
                        + " venció sin respuesta."
        );
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
}