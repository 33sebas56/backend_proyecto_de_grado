package com.ucc.convenios.companydocuments.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PublicCompanyUploadInfoResponse {

    private UUID requestId;
    private UUID convenioId;
    private String convenioCode;
    private String companyName;
    private String status;
    private Integer roundNumber;
    private LocalDateTime expiresAt;
    private List<RequiredCompanyDocumentResponse> requiredDocuments;

    public PublicCompanyUploadInfoResponse() {
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public List<RequiredCompanyDocumentResponse> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<RequiredCompanyDocumentResponse> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }
}