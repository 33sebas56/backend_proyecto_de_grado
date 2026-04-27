package com.ucc.convenios.convenios.entity;

import com.ucc.convenios.companies.entity.Company;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "convenios")
public class Convenio {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 40)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 40)
    private ConvenioStatus currentStatus = ConvenioStatus.BORRADOR;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_stage", length = 40)
    private ConvenioStage currentStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_version_id")
    private ConvenioVersion currentVersion;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "revision_issue_count", nullable = false)
    private Integer revisionIssueCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Convenio() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.currentStatus == null) {
            this.currentStatus = ConvenioStatus.BORRADOR;
        }

        if (this.revisionIssueCount == null) {
            this.revisionIssueCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();

        if (this.revisionIssueCount == null) {
            this.revisionIssueCount = 0;
        }
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public ConvenioStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ConvenioStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public ConvenioStage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(ConvenioStage currentStage) {
        this.currentStage = currentStage;
    }

    public ConvenioVersion getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(ConvenioVersion currentVersion) {
        this.currentVersion = currentVersion;
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

    public Integer getRevisionIssueCount() {
        return revisionIssueCount;
    }

    public void setRevisionIssueCount(Integer revisionIssueCount) {
        this.revisionIssueCount = revisionIssueCount;
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