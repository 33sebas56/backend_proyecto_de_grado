package com.ucc.convenios.companies.dto;

import com.ucc.convenios.companies.entity.Company;

import java.time.LocalDateTime;
import java.util.UUID;

public class CompanyResponse {

    private UUID id;
    private String nit;
    private String businessName;
    private String tradeName;
    private String identificationType;
    private String legalRepresentativeName;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String status;
    private UUID createdById;
    private UUID validatedById;
    private LocalDateTime validatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CompanyResponse() {
    }

    public static CompanyResponse fromEntity(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setNit(company.getNit());
        response.setBusinessName(company.getBusinessName());
        response.setTradeName(company.getTradeName());
        response.setIdentificationType(company.getIdentificationType());
        response.setLegalRepresentativeName(company.getLegalRepresentativeName());
        response.setContactEmail(company.getContactEmail());
        response.setContactPhone(company.getContactPhone());
        response.setAddress(company.getAddress());
        response.setStatus(company.getStatus().name());
        response.setCreatedById(company.getCreatedBy().getId());
        response.setValidatedAt(company.getValidatedAt());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());

        if (company.getValidatedBy() != null) {
            response.setValidatedById(company.getValidatedBy().getId());
        }

        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getLegalRepresentativeName() {
        return legalRepresentativeName;
    }

    public void setLegalRepresentativeName(String legalRepresentativeName) {
        this.legalRepresentativeName = legalRepresentativeName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public void setCreatedById(UUID createdById) {
        this.createdById = createdById;
    }

    public UUID getValidatedById() {
        return validatedById;
    }

    public void setValidatedById(UUID validatedById) {
        this.validatedById = validatedById;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}