package com.ucc.convenios.notifications.service;

import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.notifications.dto.ReviewAlertResponse;
import com.ucc.convenios.notifications.dto.UnreadAlertCountResponse;
import com.ucc.convenios.notifications.entity.ReviewAlert;
import com.ucc.convenios.notifications.repository.ReviewAlertRepository;
import com.ucc.convenios.shared.enums.ReviewAlertAudience;
import com.ucc.convenios.shared.enums.ReviewAlertType;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Transactional(readOnly = true)
    public UnreadAlertCountResponse countMyUnreadAlerts(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        long unreadCount = reviewAlertRepository.countByRecipientUserAndReadAtIsNull(currentUser);
        return new UnreadAlertCountResponse(unreadCount);
    }

    @Transactional
    public ReviewAlertResponse markAlertAsRead(UUID alertId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        ReviewAlert alert = reviewAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));

        validateAlertBelongsToCurrentUser(alert, currentUser);

        if (alert.getReadAt() == null) {
            alert.setReadAt(LocalDateTime.now());
        }

        return ReviewAlertResponse.fromEntity(reviewAlertRepository.save(alert));
    }

    @Transactional
    public UnreadAlertCountResponse markAllMyAlertsAsRead(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        List<ReviewAlert> alerts = reviewAlertRepository.findByRecipientUserOrderByCreatedAtDesc(currentUser);
        LocalDateTime now = LocalDateTime.now();

        for (ReviewAlert alert : alerts) {
            if (alert.getReadAt() == null) {
                alert.setReadAt(now);
            }
        }

        reviewAlertRepository.saveAll(alerts);

        return new UnreadAlertCountResponse(0);
    }

    private void validateAlertBelongsToCurrentUser(ReviewAlert alert, User currentUser) {
        if (alert.getRecipientUser() == null) {
            throw new BadRequestException("Esta alerta no está asignada directamente al usuario autenticado");
        }

        if (!alert.getRecipientUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("No puedes marcar como leída una alerta de otro usuario");
        }
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }
}