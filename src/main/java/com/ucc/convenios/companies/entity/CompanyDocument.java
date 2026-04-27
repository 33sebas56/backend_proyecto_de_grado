package com.ucc.convenios.companies.entity;

import com.ucc.convenios.shared.enums.CompanyDocumentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "company_documents")
public class CompanyDocument {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 60)
    private CompanyDocumentType documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_version_id")
    private CompanyDocumentVersion currentVersion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CompanyDocument() {
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public CompanyDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(CompanyDocumentType documentType) {
        this.documentType = documentType;
    }

    public CompanyDocumentVersion getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(CompanyDocumentVersion currentVersion) {
        this.currentVersion = currentVersion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}