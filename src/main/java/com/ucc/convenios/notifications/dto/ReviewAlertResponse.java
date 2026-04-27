package com.ucc.convenios.notifications.dto;

import com.ucc.convenios.notifications.entity.ReviewAlert;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewAlertResponse {

    private UUID id;
    private UUID approvalStepId;
    private UUID convenioId;
    private String convenioCode;
    private UUID recipientUserId;
    private String recipientUserEmail;
    private String alertType;
    private String audience;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public ReviewAlertResponse() {
    }

    public static ReviewAlertResponse fromEntity(ReviewAlert alert) {
        ReviewAlertResponse response = new ReviewAlertResponse();
        response.setId(alert.getId());
        response.setConvenioId(alert.getConvenio().getId());
        response.setConvenioCode(alert.getConvenio().getCode());
        response.setAlertType(alert.getAlertType().name());
        response.setAudience(alert.getAudience().name());
        response.setTitle(alert.getTitle());
        response.setMessage(alert.getMessage());
        response.setCreatedAt(alert.getCreatedAt());
        response.setReadAt(alert.getReadAt());

        if (alert.getApprovalStep() != null) {
            response.setApprovalStepId(alert.getApprovalStep().getId());
        }

        if (alert.getRecipientUser() != null) {
            response.setRecipientUserId(alert.getRecipientUser().getId());
            response.setRecipientUserEmail(alert.getRecipientUser().getEmail());
        }

        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getApprovalStepId() {
        return approvalStepId;
    }

    public void setApprovalStepId(UUID approvalStepId) {
        this.approvalStepId = approvalStepId;
    }

    public UUID getConvenioId() {
        return convenioId;
    }

    public void setConvenioId(UUID convenioId) {
        this.convenioId = convenioId;
    }

    public String getConvenioCode() {
        return convenioCode;
    }

    public void setConvenioCode(String convenioCode) {
        this.convenioCode = convenioCode;
    }

    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(UUID recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getRecipientUserEmail() {
        return recipientUserEmail;
    }

    public void setRecipientUserEmail(String recipientUserEmail) {
        this.recipientUserEmail = recipientUserEmail;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}