package com.ucc.convenios.convenios.dto;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.shared.enums.ConvenioType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
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
    private Integer currentVersionNumber;
    private String title;
    private String objective;
    private String description;
    private Integer durationMonths;
    private String externalEntityObligations;
    private String universityObligations;
    private BigDecimal estimatedValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer revisionIssueCount;
    private Boolean canEditBeforeReview;

    private static final Set<ConvenioStatus> EDITABLE_BEFORE_REVIEW_STATUSES = Set.of(
            ConvenioStatus.BORRADOR,
            ConvenioStatus.EMPRESA_PENDIENTE,
            ConvenioStatus.PENDIENTE_DOCUMENTOS_EMPRESA,
            ConvenioStatus.DOCUMENTOS_EMPRESA_RECIBIDOS,
            ConvenioStatus.DOCUMENTOS_OBSERVADOS_EMPRESA,
            ConvenioStatus.DOCUMENTOS_APROBADOS,
            ConvenioStatus.LISTO_PARA_RADICAR
    );

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
        response.setCanEditBeforeReview(EDITABLE_BEFORE_REVIEW_STATUSES.contains(convenio.getCurrentStatus()));

        ConvenioType type = convenio.getConvenioType() == null
                ? ConvenioType.MARCO
                : convenio.getConvenioType();

        response.setConvenioType(type.name());
        response.setConvenioTypeLabel(type.getDisplayName());
        response.setRectorSignerLabel(type.getRectorSignerLabel());

        if (convenio.getCurrentStage() != null) {
            response.setCurrentStage(convenio.getCurrentStage().name());
        }

        ConvenioVersion currentVersion = convenio.getCurrentVersion();
        if (currentVersion != null) {
            response.setCurrentVersionId(currentVersion.getId());
            response.setCurrentVersionNumber(currentVersion.getVersionNumber());
            response.setTitle(currentVersion.getTitle());
            response.setObjective(currentVersion.getObjective());
            response.setDescription(currentVersion.getDescription());
            response.setDurationMonths(currentVersion.getDurationMonths());
            response.setExternalEntityObligations(currentVersion.getExternalEntityObligations());
            response.setUniversityObligations(currentVersion.getUniversityObligations());
            response.setEstimatedValue(currentVersion.getEstimatedValue());
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

    public Integer getCurrentVersionNumber() {
        return currentVersionNumber;
    }

    public void setCurrentVersionNumber(Integer currentVersionNumber) {
        this.currentVersionNumber = currentVersionNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getExternalEntityObligations() {
        return externalEntityObligations;
    }

    public void setExternalEntityObligations(String externalEntityObligations) {
        this.externalEntityObligations = externalEntityObligations;
    }

    public String getUniversityObligations() {
        return universityObligations;
    }

    public void setUniversityObligations(String universityObligations) {
        this.universityObligations = universityObligations;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
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

    public Boolean getCanEditBeforeReview() {
        return canEditBeforeReview;
    }

    public void setCanEditBeforeReview(Boolean canEditBeforeReview) {
        this.canEditBeforeReview = canEditBeforeReview;
    }
}