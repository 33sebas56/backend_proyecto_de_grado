package com.ucc.convenios.convenios.entity;

import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.shared.enums.ConvenioGeneratedDocumentType;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "convenio_generated_documents")
public class ConvenioGeneratedDocument {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    private Convenio convenio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convenio_version_id", nullable = false)
    private ConvenioVersion convenioVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_step_id")
    private ApprovalStep approvalStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 60)
    private ConvenioGeneratedDocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", length = 40)
    private ConvenioStage stage;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "storage_path", nullable = false, columnDefinition = "TEXT")
    private String storagePath;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generated_by", nullable = false)
    private User generatedBy;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public ConvenioGeneratedDocument() {
    }

    @PrePersist
    public void prePersist() {
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
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

    public ConvenioVersion getConvenioVersion() {
        return convenioVersion;
    }

    public void setConvenioVersion(ConvenioVersion convenioVersion) {
        this.convenioVersion = convenioVersion;
    }

    public ApprovalStep getApprovalStep() {
        return approvalStep;
    }

    public void setApprovalStep(ApprovalStep approvalStep) {
        this.approvalStep = approvalStep;
    }

    public ConvenioGeneratedDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(ConvenioGeneratedDocumentType documentType) {
        this.documentType = documentType;
    }

    public ConvenioStage getStage() {
        return stage;
    }

    public void setStage(ConvenioStage stage) {
        this.stage = stage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(User generatedBy) {
        this.generatedBy = generatedBy;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}