package com.ucc.convenios.companies.dto;

import com.ucc.convenios.companies.entity.CompanyValidationHistory;

import java.time.LocalDateTime;
import java.util.UUID;

public class CompanyValidationHistoryResponse {

    private UUID id;
    private UUID companyId;
    private String previousStatus;
    private String newStatus;
    private String comment;
    private UUID performedById;
    private LocalDateTime performedAt;

    public CompanyValidationHistoryResponse() {
    }

    public static CompanyValidationHistoryResponse fromEntity(CompanyValidationHistory history) {
        CompanyValidationHistoryResponse response = new CompanyValidationHistoryResponse();
        response.setId(history.getId());
        response.setCompanyId(history.getCompany().getId());

        if (history.getPreviousStatus() != null) {
            response.setPreviousStatus(history.getPreviousStatus().name());
        }

        response.setNewStatus(history.getNewStatus().name());
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

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
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