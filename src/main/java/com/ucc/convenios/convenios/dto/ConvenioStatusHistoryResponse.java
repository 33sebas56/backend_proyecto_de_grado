package com.ucc.convenios.convenios.dto;

import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConvenioStatusHistoryResponse {

    private UUID id;
    private UUID convenioId;
    private String previousStatus;
    private String newStatus;
    private String previousStage;
    private String newStage;
    private String comment;
    private UUID performedById;
    private LocalDateTime performedAt;

    public ConvenioStatusHistoryResponse() {
    }

    public static ConvenioStatusHistoryResponse fromEntity(ConvenioStatusHistory history) {
        ConvenioStatusHistoryResponse response = new ConvenioStatusHistoryResponse();
        response.setId(history.getId());
        response.setConvenioId(history.getConvenio().getId());

        if (history.getPreviousStatus() != null) {
            response.setPreviousStatus(history.getPreviousStatus().name());
        }

        response.setNewStatus(history.getNewStatus().name());

        if (history.getPreviousStage() != null) {
            response.setPreviousStage(history.getPreviousStage().name());
        }

        if (history.getNewStage() != null) {
            response.setNewStage(history.getNewStage().name());
        }

        response.setComment(history.getComment());
        response.setPerformedById(history.getPerformedBy().getId());
        response.setPerformedAt(history.getPerformedAt());

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

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getPreviousStage() {
        return previousStage;
    }

    public void setPreviousStage(String previousStage) {
        this.previousStage = previousStage;
    }

    public String getNewStage() {
        return newStage;
    }

    public void setNewStage(String newStage) {
        this.newStage = newStage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getPerformedById() {
        return performedById;
    }

    public void setPerformedById(UUID performedById) {
        this.performedById = performedById;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
}