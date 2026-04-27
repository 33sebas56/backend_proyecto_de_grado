package com.ucc.convenios.approvals.dto;

import com.ucc.convenios.approvals.entity.ApprovalRound;

import java.time.LocalDateTime;
import java.util.UUID;

public class ApprovalRoundResponse {

    private UUID id;
    private UUID convenioId;
    private UUID convenioVersionId;
    private Integer roundNumber;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public ApprovalRoundResponse() {
    }

    public static ApprovalRoundResponse fromEntity(ApprovalRound round) {
        ApprovalRoundResponse response = new ApprovalRoundResponse();
        response.setId(round.getId());
        response.setConvenioId(round.getConvenio().getId());
        response.setConvenioVersionId(round.getConvenioVersion().getId());
        response.setRoundNumber(round.getRoundNumber());
        response.setStatus(round.getStatus().name());
        response.setStartedAt(round.getStartedAt());
        response.setFinishedAt(round.getFinishedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}