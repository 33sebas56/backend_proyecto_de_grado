package com.ucc.convenios.approvals.entity;

import com.ucc.convenios.shared.enums.ApprovalStepStatus;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "approval_steps",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_approval_steps_round_stage",
                        columnNames = {"approval_round_id", "stage"}
                )
        }
)
public class ApprovalStep {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approval_round_id", nullable = false)
    private ApprovalRound approvalRound;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false, length = 40)
    private ConvenioStage stage;

    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_user_id", nullable = false)
    private User assignedUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ApprovalStepStatus status = ApprovalStepStatus.PENDIENTE;

    @Column(name = "decision_comment", columnDefinition = "TEXT")
    private String decisionComment;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "approval_code", length = 80)
    private String approvalCode;

    @Column(name = "seal_text", columnDefinition = "TEXT")
    private String sealText;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "first_reminder_sent_at")
    private LocalDateTime firstReminderSentAt;

    @Column(name = "second_reminder_sent_at")
    private LocalDateTime secondReminderSentAt;

    @Column(name = "final_reminder_sent_at")
    private LocalDateTime finalReminderSentAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    public ApprovalStep() {
    }

    @PrePersist
    public void prePersist() {
        if (this.assignedAt == null) {
            this.assignedAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = ApprovalStepStatus.PENDIENTE;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ApprovalRound getApprovalRound() {
        return approvalRound;
    }

    public void setApprovalRound(ApprovalRound approvalRound) {
        this.approvalRound = approvalRound;
    }

    public ConvenioStage getStage() {
        return stage;
    }

    public void setStage(ConvenioStage stage) {
        this.stage = stage;
    }

    public Integer getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(Integer stageOrder) {
        this.stageOrder = stageOrder;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    public ApprovalStepStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStepStatus status) {
        this.status = status;
    }

    public String getDecisionComment() {
        return decisionComment;
    }

    public void setDecisionComment(String decisionComment) {
        this.decisionComment = decisionComment;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getSealText() {
        return sealText;
    }

    public void setSealText(String sealText) {
        this.sealText = sealText;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public LocalDateTime getFirstReminderSentAt() {
        return firstReminderSentAt;
    }

    public void setFirstReminderSentAt(LocalDateTime firstReminderSentAt) {
        this.firstReminderSentAt = firstReminderSentAt;
    }

    public LocalDateTime getSecondReminderSentAt() {
        return secondReminderSentAt;
    }

    public void setSecondReminderSentAt(LocalDateTime secondReminderSentAt) {
        this.secondReminderSentAt = secondReminderSentAt;
    }

    public LocalDateTime getFinalReminderSentAt() {
        return finalReminderSentAt;
    }

    public void setFinalReminderSentAt(LocalDateTime finalReminderSentAt) {
        this.finalReminderSentAt = finalReminderSentAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}