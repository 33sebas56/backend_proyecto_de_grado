package com.ucc.convenios.convenios.dto;

import com.ucc.convenios.shared.enums.ConvenioType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateConvenioRequest {

    @NotNull(message = "La empresa es obligatoria")
    private UUID companyId;

    @NotNull(message = "El tipo de convenio es obligatorio")
    private ConvenioType convenioType;

    @NotBlank(message = "El título del convenio es obligatorio")
    @Size(max = 200, message = "El título no puede superar 200 caracteres")
    private String title;

    @NotBlank(message = "El objeto del convenio es obligatorio")
    private String objective;

    private String description;

    @NotNull(message = "La duración del convenio es obligatoria")
    @Min(value = 1, message = "La duración del convenio debe ser mínimo de 1 mes")
    private Integer durationMonths;

    private String externalEntityObligations;

    private String universityObligations;

    @DecimalMin(value = "0.0", inclusive = true, message = "El valor estimado no puede ser negativo")
    private BigDecimal estimatedValue;

    public CreateConvenioRequest() {
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public ConvenioType getConvenioType() {
        return convenioType;
    }

    public void setConvenioType(ConvenioType convenioType) {
        this.convenioType = convenioType;
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
}