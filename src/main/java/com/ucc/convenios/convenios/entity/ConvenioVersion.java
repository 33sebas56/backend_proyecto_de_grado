package com.ucc.convenios.convenios.entity;

import com.ucc.convenios.shared.enums.ConvenioVersionReason;
import com.ucc.convenios.shared.enums.ConvenioVersionStatus;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "convenio_versions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_convenio_version",
                        columnNames = {"convenio_id", "version_number"}
                )
        }
)
public class ConvenioVersion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    private Convenio convenio;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "objective", nullable = false, columnDefinition = "TEXT")
    private String objective;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "external_entity_obligations", columnDefinition = "TEXT")
    private String externalEntityObligations;

    @Column(name = "university_obligations", columnDefinition = "TEXT")
    private String universityObligations;

    @Column(name = "estimated_value", precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    @Column(name = "generated_pdf_url", columnDefinition = "TEXT")
    private String generatedPdfUrl;

    @Column(name = "generated_pdf_storage_path", columnDefinition = "TEXT")
    private String generatedPdfStoragePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private ConvenioVersionStatus status = ConvenioVersionStatus.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 80)
    private ConvenioVersionReason reason = ConvenioVersionReason.CREACION_INICIAL;

    public ConvenioVersion() {
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = ConvenioVersionStatus.BORRADOR;
        }

        if (this.reason == null) {
            this.reason = ConvenioVersionReason.CREACION_INICIAL;
        }

        normalizeFields();
    }

    @PreUpdate
    public void preUpdate() {
        normalizeFields();
    }

    private void normalizeFields() {
        if (this.title != null) {
            this.title = this.title.trim();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
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

    public ConvenioVersionStatus getStatus() {
        return status;
    }

    public void setStatus(ConvenioVersionStatus status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ConvenioVersionReason getReason() {
        return reason;
    }

    public void setReason(ConvenioVersionReason reason) {
        this.reason = reason;
    }
}