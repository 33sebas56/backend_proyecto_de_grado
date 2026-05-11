package com.ucc.convenios.companydocuments.dto;

import com.ucc.convenios.companydocuments.entity.CompanyDocumentRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public class CompanyDocumentRequestResponse {

    private UUID id;
    private UUID convenioId;
    private UUID companyId;
    private Integer roundNumber;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private UUID reviewedById;
    private String reviewComment;

    public CompanyDocumentRequestResponse() {
    }

    public static CompanyDocumentRequestResponse fromEntity(CompanyDocumentRequest request) {
        CompanyDocumentRequestResponse response = new CompanyDocumentRequestResponse();
        response.setId(request.getId());
        response.setConvenioId(request.getConvenio().getId());
        response.setCompanyId(request.getCompany().getId());
        response.setRoundNumber(request.getRoundNumber());
        response.setStatus(request.getStatus().name());
        response.setRequestedAt(request.getRequestedAt());
        response.setSubmittedAt(request.getSubmittedAt());
        response.setReviewedAt(request.getReviewedAt());
        response.setReviewComment(request.getReviewComment());

        if (request.getReviewedBy() != null) {
            response.setReviewedById(request.getReviewedBy().getId());
        }

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

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
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

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public UUID getReviewedById() {
        return reviewedById;
    }

    public void setReviewedById(UUID reviewedById) {
        this.reviewedById = reviewedById;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
}