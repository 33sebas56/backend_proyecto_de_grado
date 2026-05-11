package com.ucc.convenios.dashboard.service;

import com.ucc.convenios.approvals.repository.ApprovalStepRepository;
import com.ucc.convenios.companydocuments.entity.CompanySubmittedDocument;
import com.ucc.convenios.companydocuments.repository.CompanySubmittedDocumentRepository;
import com.ucc.convenios.companies.repository.CompanyRepository;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;
import com.ucc.convenios.convenios.repository.ConvenioRepository;
import com.ucc.convenios.convenios.repository.ConvenioStatusHistoryRepository;
import com.ucc.convenios.dashboard.dto.*;
import com.ucc.convenios.notifications.dto.ReviewAlertResponse;
import com.ucc.convenios.notifications.repository.ReviewAlertRepository;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.roles.repository.UserRoleRepository;
import com.ucc.convenios.shared.enums.ApprovalStepStatus;
import com.ucc.convenios.shared.enums.CompanyStatus;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.ReviewerProfile;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.ReviewerProfileRepository;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ReviewerProfileRepository reviewerProfileRepository;
    private final ConvenioRepository convenioRepository;
    private final CompanyRepository companyRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final ReviewAlertRepository reviewAlertRepository;
    private final ConvenioStatusHistoryRepository convenioStatusHistoryRepository;
    private final CompanySubmittedDocumentRepository companySubmittedDocumentRepository;

    public DashboardService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            ReviewerProfileRepository reviewerProfileRepository,
            ConvenioRepository convenioRepository,
            CompanyRepository companyRepository,
            ApprovalStepRepository approvalStepRepository,
            ReviewAlertRepository reviewAlertRepository,
            ConvenioStatusHistoryRepository convenioStatusHistoryRepository,
            CompanySubmittedDocumentRepository companySubmittedDocumentRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.reviewerProfileRepository = reviewerProfileRepository;
        this.convenioRepository = convenioRepository;
        this.companyRepository = companyRepository;
        this.approvalStepRepository = approvalStepRepository;
        this.reviewAlertRepository = reviewAlertRepository;
        this.convenioStatusHistoryRepository = convenioStatusHistoryRepository;
        this.companySubmittedDocumentRepository = companySubmittedDocumentRepository;
    }

    @Transactional(readOnly = true)
    public MyDashboardResponse getMyDashboard(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        List<String> roles = getRoleNames(currentUser);
        List<ReviewerProfile> profiles = reviewerProfileRepository.findByUser(currentUser);

        long myActiveCases = profiles.stream()
                .map(ReviewerProfile::getCurrentActiveCases)
                .filter(value -> value != null)
                .mapToLong(Integer::longValue)
                .sum();

        MyDashboardSummaryResponse summary = new MyDashboardSummaryResponse(
                approvalStepRepository.countByAssignedUserAndStatus(currentUser, ApprovalStepStatus.PENDIENTE),
                reviewAlertRepository.countByRecipientUser(currentUser),
                reviewAlertRepository.countByRecipientUserAndReadAtIsNull(currentUser),
                myActiveCases,
                convenioRepository.countByCreatedBy(currentUser),
                convenioRepository.countByCreatedByAndCurrentStatus(currentUser, ConvenioStatus.EN_CORRECCION),
                convenioRepository.countByCreatedByAndCurrentStatus(currentUser, ConvenioStatus.PENDIENTE_DOCUMENTOS_EMPRESA),
                convenioRepository.countByCreatedByAndCurrentStatus(currentUser, ConvenioStatus.LISTO_PARA_RADICAR)
        );

        List<ReviewerProfileDashboardResponse> reviewerProfiles = profiles.stream()
                .map(ReviewerProfileDashboardResponse::fromEntity)
                .toList();

        return new MyDashboardResponse(
                DashboardUserResponse.fromEntity(currentUser, roles),
                reviewerProfiles,
                summary
        );
    }

    @Transactional(readOnly = true)
    public DashboardWorkResponse getMyWork(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        List<DashboardPendingApprovalResponse> pendingApprovals = approvalStepRepository
                .findByAssignedUserAndStatusOrderByAssignedAtDesc(currentUser, ApprovalStepStatus.PENDIENTE)
                .stream()
                .map(DashboardPendingApprovalResponse::fromEntity)
                .toList();

        List<ReviewAlertResponse> alerts = reviewAlertRepository
                .findByRecipientUserOrderByCreatedAtDesc(currentUser, PageRequest.of(0, 10))
                .stream()
                .map(ReviewAlertResponse::fromEntity)
                .toList();

        List<DashboardRecentConvenioResponse> recentCreatedConvenios = convenioRepository
                .findByCreatedByOrderByCreatedAtDesc(currentUser, PageRequest.of(0, 10))
                .stream()
                .map(DashboardRecentConvenioResponse::fromEntity)
                .toList();

        return new DashboardWorkResponse(
                pendingApprovals,
                alerts,
                recentCreatedConvenios
        );
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getAdminSummary() {
        return new DashboardSummaryResponse(
                userRepository.count(),
                userRepository.countByActiveTrue(),
                companyRepository.count(),
                companyRepository.countByStatus(CompanyStatus.PENDIENTE_VALIDACION),
                convenioRepository.count(),
                buildConveniosByStatus(),
                approvalStepRepository.countByStatus(ApprovalStepStatus.PENDIENTE),
                reviewAlertRepository.countByReadAtIsNull()
        );
    }

    @Transactional(readOnly = true)
    public List<DashboardActivityResponse> getRecentActivity() {
        List<DashboardActivityResponse> activities = new ArrayList<>();

        List<ConvenioStatusHistory> histories = convenioStatusHistoryRepository
                .findAllByOrderByPerformedAtDesc(PageRequest.of(0, 10));

        for (ConvenioStatusHistory history : histories) {
            Convenio convenio = history.getConvenio();

            String description = "Estado nuevo: " + history.getNewStatus().name();

            if (history.getNewStage() != null) {
                description = description + " / Etapa: " + history.getNewStage().name();
            }

            if (history.getComment() != null && !history.getComment().isBlank()) {
                description = description + " / " + history.getComment();
            }

            activities.add(new DashboardActivityResponse(
                    "CONVENIO_STATUS",
                    "Cambio de estado del convenio",
                    description,
                    convenio.getId(),
                    convenio.getCode(),
                    history.getPerformedBy().getFullName(),
                    history.getPerformedAt()
            ));
        }

        List<CompanySubmittedDocument> documents = companySubmittedDocumentRepository
                .findAllByOrderByUploadedAtDesc(PageRequest.of(0, 10));

        for (CompanySubmittedDocument document : documents) {
            Convenio convenio = document.getConvenio();

            activities.add(new DashboardActivityResponse(
                    "DOCUMENT_UPLOADED",
                    "Documento cargado por empresa",
                    document.getDisplayName() + " - " + document.getOriginalFilename(),
                    convenio.getId(),
                    convenio.getCode(),
                    convenio.getCompany().getBusinessName(),
                    document.getUploadedAt()
            ));
        }

        return activities.stream()
                .sorted(Comparator.comparing(DashboardActivityResponse::createdAt).reversed())
                .limit(10)
                .toList();
    }

    private Map<String, Long> buildConveniosByStatus() {
        Map<String, Long> counts = new LinkedHashMap<>();

        for (ConvenioStatus status : ConvenioStatus.values()) {
            counts.put(status.name(), convenioRepository.countByCurrentStatus(status));
        }

        return counts;
    }

    private List<String> getRoleNames(User user) {
        return userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .map(role -> role.getName())
                .toList();
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }
}