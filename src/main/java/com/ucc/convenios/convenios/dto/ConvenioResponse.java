package com.ucc.convenios.convenios.dto;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.shared.enums.ConvenioType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ConvenioResponse {

    private UUID id;
    private String code;
    private UUID companyId;
    private String companyNit;
    private String companyBusinessName;
    private UUID createdById;
    private String currentStatus;
    private String currentStage;
    private String convenioType;
    private String convenioTypeLabel;
    private String rectorSignerLabel;
    private UUID currentVersionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer revisionIssueCount;

    public ConvenioResponse() {
    }

    public static ConvenioResponse fromEntity(Convenio convenio) {
        ConvenioResponse response = new ConvenioResponse();
        response.setId(convenio.getId());
        response.setCode(convenio.getCode());
        response.setCompanyId(convenio.getCompany().getId());
        response.setCompanyNit(convenio.getCompany().getNit());
        response.setCompanyBusinessName(convenio.getCompany().getBusinessName());
        response.setCreatedById(convenio.getCreatedBy().getId());
        response.setCurrentStatus(convenio.getCurrentStatus().name());
        response.setRevisionIssueCount(convenio.getRevisionIssueCount());

        ConvenioType type = convenio.getConvenioType() == null
                ? ConvenioType.MARCO
                : convenio.getConvenioType();

        response.setConvenioType(type.name());
        response.setConvenioTypeLabel(type.getDisplayName());
        response.setRectorSignerLabel(type.getRectorSignerLabel());

        if (convenio.getCurrentStage() != null) {
            response.setCurrentStage(convenio.getCurrentStage().name());
        }

        if (convenio.getCurrentVersion() != null) {
            response.setCurrentVersionId(convenio.getCurrentVersion().getId());
        }

        response.setStartDate(convenio.getStartDate());
        response.setEndDate(convenio.getEndDate());
        response.setCreatedAt(convenio.getCreatedAt());
        response.setUpdatedAt(convenio.getUpdatedAt());

        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public String getCompanyNit() {
        return companyNit;
    }

    public void setCompanyNit(String companyNit) {
        this.companyNit = companyNit;
    }

    public String getCompanyBusinessName() {
        return companyBusinessName;
    }

    public void setCompanyBusinessName(String companyBusinessName) {
        this.companyBusinessName = companyBusinessName;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public void setCreatedById(UUID createdById) {
        this.createdById = createdById;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getConvenioType() {
        return convenioType;
    }

    public void setConvenioType(String convenioType) {
        this.convenioType = convenioType;
    }

    public String getConvenioTypeLabel() {
        return convenioTypeLabel;
    }

    public void setConvenioTypeLabel(String convenioTypeLabel) {
        this.convenioTypeLabel = convenioTypeLabel;
    }

    public String getRectorSignerLabel() {
        return rectorSignerLabel;
    }

    public void setRectorSignerLabel(String rectorSignerLabel) {
        this.rectorSignerLabel = rectorSignerLabel;
    }

    public UUID getCurrentVersionId() {
        return currentVersionId;
    }

    public void setCurrentVersionId(UUID currentVersionId) {
        this.currentVersionId = currentVersionId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public Integer getRevisionIssueCount() {
        return revisionIssueCount;
    }

    public void setRevisionIssueCount(Integer revisionIssueCount) {
        this.revisionIssueCount = revisionIssueCount;
    }
}