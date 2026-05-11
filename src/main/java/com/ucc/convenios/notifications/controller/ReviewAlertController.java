package com.ucc.convenios.notifications.controller;

import com.ucc.convenios.approvals.service.ApprovalDeadlineService;
import com.ucc.convenios.notifications.dto.ReviewAlertResponse;
import com.ucc.convenios.notifications.dto.UnreadAlertCountResponse;
import com.ucc.convenios.notifications.service.ReviewAlertService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/review-alerts")
public class ReviewAlertController {

    private final ReviewAlertService reviewAlertService;
    private final ApprovalDeadlineService approvalDeadlineService;

    public ReviewAlertController(
            ReviewAlertService reviewAlertService,
            ApprovalDeadlineService approvalDeadlineService
    ) {
        this.reviewAlertService = reviewAlertService;
        this.approvalDeadlineService = approvalDeadlineService;
    }

    @GetMapping("/me")
    public List<ReviewAlertResponse> findMyAlerts(Authentication authentication) {
        return reviewAlertService.findMyAlerts(authentication);
    }

    @GetMapping("/unread-count")
    public UnreadAlertCountResponse countMyUnreadAlerts(Authentication authentication) {
        return reviewAlertService.countMyUnreadAlerts(authentication);
    }

    @PostMapping("/{alertId}/read")
    public ReviewAlertResponse markAlertAsRead(
            @PathVariable UUID alertId,
            Authentication authentication
    ) {
        return reviewAlertService.markAlertAsRead(alertId, authentication);
    }

    @PostMapping("/read-all")
    public UnreadAlertCountResponse markAllMyAlertsAsRead(Authentication authentication) {
        return reviewAlertService.markAllMyAlertsAsRead(authentication);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReviewAlertResponse> findAdminAlerts() {
        return reviewAlertService.findAdminAlerts();
    }

    @GetMapping("/proyeccion")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GESTOR_PROYECCION')")
    public List<ReviewAlertResponse> findProjectionAlerts() {
        return reviewAlertService.findProjectionAlerts();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReviewAlertResponse> findAllAlerts() {
        return reviewAlertService.findAllAlerts();
    }

    @PostMapping("/check-deadlines")
    @PreAuthorize("hasRole('ADMIN')")
    public String checkDeadlines() {
        approvalDeadlineService.processPendingDeadlines();
        return "Revisión de vencimientos ejecutada correctamente";
    }
}