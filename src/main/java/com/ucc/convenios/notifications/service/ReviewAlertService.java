package com.ucc.convenios.notifications.service;

import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.notifications.dto.ReviewAlertResponse;
import com.ucc.convenios.notifications.entity.ReviewAlert;
import com.ucc.convenios.notifications.repository.ReviewAlertRepository;
import com.ucc.convenios.shared.enums.ReviewAlertAudience;
import com.ucc.convenios.shared.enums.ReviewAlertType;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewAlertService {

    private final ReviewAlertRepository reviewAlertRepository;
    private final UserRepository userRepository;

    public ReviewAlertService(
            ReviewAlertRepository reviewAlertRepository,
            UserRepository userRepository
    ) {
        this.reviewAlertRepository = reviewAlertRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewAlert createAlert(
            ApprovalStep approvalStep,
            Convenio convenio,
            User recipientUser,
            ReviewAlertType alertType,
            ReviewAlertAudience audience,
            String title,
            String message
    ) {
        ReviewAlert alert = new ReviewAlert();
        alert.setApprovalStep(approvalStep);
        alert.setConvenio(convenio);
        alert.setRecipientUser(recipientUser);
        alert.setAlertType(alertType);
        alert.setAudience(audience);
        alert.setTitle(title);
        alert.setMessage(message);

        return reviewAlertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public List<ReviewAlertResponse> findMyAlerts(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        return reviewAlertRepository.findByRecipientUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(ReviewAlertResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewAlertResponse> findAdminAlerts() {
        return reviewAlertRepository.findByAudienceOrderByCreatedAtDesc(ReviewAlertAudience.ADMIN)
                .stream()
                .map(ReviewAlertResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewAlertResponse> findProjectionAlerts() {
        return reviewAlertRepository.findByAudienceOrderByCreatedAtDesc(ReviewAlertAudience.PROYECCION_SOCIAL)
                .stream()
                .map(ReviewAlertResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewAlertResponse> findAllAlerts() {
        return reviewAlertRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ReviewAlertResponse::fromEntity)
                .toList();
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }
}