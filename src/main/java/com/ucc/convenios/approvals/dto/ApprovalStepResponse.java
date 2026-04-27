package com.ucc.convenios.approvals.dto;

import com.ucc.convenios.approvals.entity.ApprovalStep;

import java.time.LocalDateTime;
import java.util.UUID;

public class ApprovalStepResponse {

    private UUID id;
    private UUID approvalRoundId;
    private UUID convenioId;
    private UUID convenioVersionId;
    private String convenioCode;
    private String convenioTitle;
    private String stage;
    private Integer stageOrder;
    private UUID assignedUserId;
    private String assignedUserEmail;
    private String status;
    private String decisionComment;
    private LocalDateTime assignedAt;
    private LocalDateTime respondedAt;
    private String approvalCode;
    private String sealText;

    public ApprovalStepResponse() {
    }

    public static ApprovalStepResponse fromEntity(ApprovalStep step) {
        ApprovalStepResponse response = new ApprovalStepResponse();
        response.setId(step.getId());
        response.setApprovalRoundId(step.getApprovalRound().getId());
        response.setConvenioId(step.getApprovalRound().getConvenio().getId());
        response.setConvenioVersionId(step.getApprovalRound().getConvenioVersion().getId());
        response.setConvenioCode(step.getApprovalRound().getConvenio().getCode());
        response.setConvenioTitle(step.getApprovalRound().getConvenioVersion().getTitle());
        response.setStage(step.getStage().name());
        response.setStageOrder(step.getStageOrder());
        response.setAssignedUserId(step.getAssignedUser().getId());
        response.setAssignedUserEmail(step.getAssignedUser().getEmail());
        response.setStatus(step.getStatus().name());
        response.setDecisionComment(step.getDecisionComment());
        response.setAssignedAt(step.getAssignedAt());
        response.setRespondedAt(step.getRespondedAt());
        response.setApprovalCode(step.getApprovalCode());
        response.setSealText(step.getSealText());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getApprovalRoundId() {
        return approvalRoundId;
    }

    public void setApprovalRoundId(UUID approvalRoundId) {
        this.approvalRoundId = approvalRoundId;
    }

    public UUID getConvenioId() {
        return convenioId;
    }

    public void setConvenioId(UUID convenioId) {
        this.convenioId = convenioId;
    }

    public UUID getConvenioVersionId() {
        return convenioVersionId;
    }

    public void setConvenioVersionId(UUID convenioVersionId) {
        this.convenioVersionId = convenioVersionId;
    }

    public String getConvenioCode() {
        return convenioCode;
    }

    public void setConvenioCode(String convenioCode) {
        this.convenioCode = convenioCode;
    }

    public String getConvenioTitle() {
        return convenioTitle;
    }

    public void setConvenioTitle(String convenioTitle) {
        this.convenioTitle = convenioTitle;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Integer getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(Integer stageOrder) {
        this.stageOrder = stageOrder;
    }

    public UUID getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(UUID assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getAssignedUserEmail() {
        return assignedUserEmail;
    }

    public void setAssignedUserEmail(String assignedUserEmail) {
        this.assignedUserEmail = assignedUserEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}