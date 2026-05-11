package com.ucc.convenios.notifications.service;

import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.companydocuments.entity.CompanyUploadToken;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.users.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConvenioNotificationService {

    private static final int COMPANY_UPLOAD_TOKEN_EXPIRATION_DAYS = 15;

    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;
    private final AppLinkService appLinkService;

    public ConvenioNotificationService(
            MailService mailService,
            EmailTemplateService emailTemplateService,
            AppLinkService appLinkService
    ) {
        this.mailService = mailService;
        this.emailTemplateService = emailTemplateService;
        this.appLinkService = appLinkService;
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
}