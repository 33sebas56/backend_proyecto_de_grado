package com.ucc.convenios.notifications.entity;

import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.shared.enums.ReviewAlertAudience;
import com.ucc.convenios.shared.enums.ReviewAlertType;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_alerts")
public class ReviewAlert {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_step_id")
    private ApprovalStep approvalStep;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    private Convenio convenio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 60)
    private ReviewAlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "audience", nullable = false, length = 60)
    private ReviewAlertAudience audience;

    @Column(name = "title", nullable = false, length = 180)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public ReviewAlert() {
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ApprovalStep getApprovalStep() {
        return approvalStep;
    }

    public void setApprovalStep(ApprovalStep approvalStep) {
        this.approvalStep = approvalStep;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public ReviewAlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(ReviewAlertType alertType) {
        this.alertType = alertType;
    }

    public ReviewAlertAudience getAudience() {
        return audience;
    }

    public void setAudience(ReviewAlertAudience audience) {
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