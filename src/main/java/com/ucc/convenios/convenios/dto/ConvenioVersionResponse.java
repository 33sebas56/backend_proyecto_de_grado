package com.ucc.convenios.convenios.dto;

import com.ucc.convenios.convenios.entity.ConvenioVersion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ConvenioVersionResponse {

    private UUID id;
    private UUID convenioId;
    private Integer versionNumber;
    private String title;
    private String objective;
    private String description;
    private Integer durationMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private String externalEntityObligations;
    private String universityObligations;
    private BigDecimal estimatedValue;
    private String generatedPdfUrl;
    private String generatedPdfStoragePath;
    private String status;
    private UUID createdById;
    private LocalDateTime createdAt;
    private String reason;

    public ConvenioVersionResponse() {
    }

    public static ConvenioVersionResponse fromEntity(ConvenioVersion version) {
        ConvenioVersionResponse response = new ConvenioVersionResponse();
        response.setId(version.getId());
        response.setConvenioId(version.getConvenio().getId());
        response.setVersionNumber(version.getVersionNumber());
        response.setTitle(version.getTitle());
        response.setObjective(version.getObjective());
        response.setDescription(version.getDescription());
        response.setDurationMonths(version.getDurationMonths());
        response.setStartDate(version.getStartDate());
        response.setEndDate(version.getEndDate());
        response.setExternalEntityObligations(version.getExternalEntityObligations());
        response.setUniversityObligations(version.getUniversityObligations());
        response.setEstimatedValue(version.getEstimatedValue());
        response.setGeneratedPdfUrl(version.getGeneratedPdfUrl());
        response.setGeneratedPdfStoragePath(version.getGeneratedPdfStoragePath());
        response.setStatus(version.getStatus().name());
        response.setCreatedById(version.getCreatedBy().getId());
        response.setCreatedAt(version.getCreatedAt());
        response.setReason(version.getReason().name());
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

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
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

    public String getGeneratedPdfUrl() {
        return generatedPdfUrl;
    }

    public void setGeneratedPdfUrl(String generatedPdfUrl) {
        this.generatedPdfUrl = generatedPdfUrl;
    }

    public String getGeneratedPdfStoragePath() {
        return generatedPdfStoragePath;
    }

    public void setGeneratedPdfStoragePath(String generatedPdfStoragePath) {
        this.generatedPdfStoragePath = generatedPdfStoragePath;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}