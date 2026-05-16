package com.ucc.convenios.notifications.service;

import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.companydocuments.entity.CompanyUploadToken;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.RoleRepository;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.enums.ConvenioType;
import com.ucc.convenios.shared.enums.ReviewAlertAudience;
import com.ucc.convenios.shared.enums.ReviewAlertType;
import com.ucc.convenios.users.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConvenioNotificationService {

    private static final int COMPANY_UPLOAD_TOKEN_EXPIRATION_DAYS = 15;
    private static final String PROJECTION_ROLE_NAME = "GESTOR_PROYECCION";

    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;
    private final AppLinkService appLinkService;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ReviewAlertService reviewAlertService;

    public ConvenioNotificationService(
            MailService mailService,
            EmailTemplateService emailTemplateService,
            AppLinkService appLinkService,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            ReviewAlertService reviewAlertService
    ) {
        this.mailService = mailService;
        this.emailTemplateService = emailTemplateService;
        this.appLinkService = appLinkService;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.reviewAlertService = reviewAlertService;
    }

    public void notifyCompanyDocumentRequest(
            Convenio convenio,
            CompanyUploadToken token,
            String rawToken,
            List<String> requiredDocuments
    ) {
        String uploadUrl = appLinkService.buildCompanyUploadUrl(rawToken);
        String companyName = convenio.getCompany().getBusinessName();
        String convenioCode = convenio.getCode();

        String subject = emailTemplateService.buildCompanyDocumentUploadSubject(convenioCode);
        String htmlBody = emailTemplateService.buildCompanyDocumentUploadHtml(
                convenioCode,
                companyName,
                uploadUrl,
                COMPANY_UPLOAD_TOKEN_EXPIRATION_DAYS,
                requiredDocuments
        );

        mailService.sendHtmlEmail(token.getRecipientEmail(), subject, htmlBody);
    }

    public void notifyResponsibleCompanyUploadedDocuments(Convenio convenio) {
        User responsible = convenio.getCreatedBy();
        String convenioCode = convenio.getCode();
        String companyName = convenio.getCompany().getBusinessName();
        String systemUrl = appLinkService.buildSystemUrl();

        String subject = emailTemplateService.buildResponsibleDocumentsReceivedSubject(convenioCode);
        String htmlBody = emailTemplateService.buildResponsibleDocumentsReceivedHtml(
                convenioCode,
                companyName,
                systemUrl
        );

        mailService.sendHtmlEmail(responsible.getEmail(), subject, htmlBody);
    }

    public void notifyEarlyCorrectionLimit(Convenio convenio) {
        User responsible = convenio.getCreatedBy();
        String convenioCode = convenio.getCode();
        String systemUrl = appLinkService.buildSystemUrl();

        String subject = emailTemplateService.buildEarlyCorrectionLimitSubject(convenioCode);
        String htmlBody = emailTemplateService.buildEarlyCorrectionLimitHtml(convenioCode, systemUrl);

        mailService.sendHtmlEmail(responsible.getEmail(), subject, htmlBody);
    }

    public void notifyEarlyDocumentProcessDiscarded(Convenio convenio, String comment) {
        User responsible = convenio.getCreatedBy();
        String convenioCode = convenio.getCode();
        String systemUrl = appLinkService.buildSystemUrl();

        String subject = emailTemplateService.buildEarlyDocumentProcessDiscardedSubject(convenioCode);
        String htmlBody = emailTemplateService.buildEarlyDocumentProcessDiscardedHtml(
                convenioCode,
                comment,
                systemUrl
        );

        mailService.sendHtmlEmail(responsible.getEmail(), subject, htmlBody);
    }

    public void notifyReviewerAssigned(ApprovalStep step) {
        User reviewer = step.getAssignedUser();
        Convenio convenio = step.getApprovalRound().getConvenio();
        ConvenioStage stage = step.getStage();
        String convenioCode = convenio.getCode();
        String companyName = convenio.getCompany().getBusinessName();
        String systemUrl = appLinkService.buildSystemUrl();

        String subject = emailTemplateService.buildReviewerAssignedSubject(convenioCode, stage);
        String htmlBody = emailTemplateService.buildReviewerAssignedHtml(
                convenioCode,
                companyName,
                stage,
                step.getDueAt(),
                systemUrl
        );

        mailService.sendHtmlEmail(reviewer.getEmail(), subject, htmlBody);
    }

    public void notifyProjectionFormalizationPending(Convenio convenio) {
        Role projectionRole = roleRepository.findByName(PROJECTION_ROLE_NAME).orElse(null);

        if (projectionRole == null) {
            return;
        }

        List<User> projectionUsers = userRoleRepository.findByRole(projectionRole)
                .stream()
                .map(UserRole::getUser)
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .toList();

        if (projectionUsers.isEmpty()) {
            return;
        }

        String convenioCode = convenio.getCode();
        String companyName = convenio.getCompany().getBusinessName();
        String convenioTypeLabel = getConvenioTypeLabel(convenio);
        Integer durationMonths = getDurationMonths(convenio);
        String systemUrl = appLinkService.buildSystemUrl();

        String subject = emailTemplateService.buildFormalizationPendingSubject(convenioCode);
        String htmlBody = emailTemplateService.buildFormalizationPendingHtml(
                convenioCode,
                companyName,
                convenioTypeLabel,
                durationMonths,
                systemUrl
        );

        for (User projectionUser : projectionUsers) {
            reviewAlertService.createAlert(
                    null,
                    convenio,
                    projectionUser,
                    ReviewAlertType.CONVENIO_PENDIENTE_FORMALIZACION,
                    ReviewAlertAudience.PROYECCION_SOCIAL,
                    "Convenio pendiente de formalización",
                    "El convenio " + convenioCode + " fue aprobado para firma y debe ser formalizado por Proyección Social."
            );

            mailService.sendHtmlEmail(projectionUser.getEmail(), subject, htmlBody);
        }
    }

    private String getConvenioTypeLabel(Convenio convenio) {
        ConvenioType type = convenio.getConvenioType() == null
                ? ConvenioType.MARCO
                : convenio.getConvenioType();

        return type.getDisplayName();
    }

    private Integer getDurationMonths(Convenio convenio) {
        ConvenioVersion version = convenio.getCurrentVersion();
        return version == null ? null : version.getDurationMonths();
    }
}