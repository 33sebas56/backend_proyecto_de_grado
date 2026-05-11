package com.ucc.convenios.companydocuments.entity;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.shared.enums.CompanyExternalDocumentType;
import com.ucc.convenios.shared.enums.CompanySubmittedDocumentStatus;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "company_submitted_documents")
public class CompanySubmittedDocument {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private CompanyDocumentRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    private Convenio convenio;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 80)
    private CompanyExternalDocumentType documentType;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "mime_type", length = 120)
    private String mimeType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "storage_path", nullable = false, columnDefinition = "TEXT")
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CompanySubmittedDocumentStatus status = CompanySubmittedDocumentStatus.SUBIDO;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_by_document_id")
    private CompanySubmittedDocument replacedByDocument;

    @Column(name = "deleted_from_storage_at")
    private LocalDateTime deletedFromStorageAt;

    @Column(name = "deletion_reason", columnDefinition = "TEXT")
    private String deletionReason;

    public CompanySubmittedDocument() {
    }

    @PrePersist
    public void prePersist() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = CompanySubmittedDocumentStatus.SUBIDO;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CompanyDocumentRequest getRequest() {
        return request;
    }

    public void setRequest(CompanyDocumentRequest request) {
        this.request = request;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public CompanyExternalDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(CompanyExternalDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public CompanySubmittedDocumentStatus getStatus() {
        return status;
    }

    public void setStatus(CompanySubmittedDocumentStatus status) {
        this.status = status;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public CompanySubmittedDocument getReplacedByDocument() {
        return replacedByDocument;
    }

    public void setReplacedByDocument(CompanySubmittedDocument replacedByDocument) {
        this.replacedByDocument = replacedByDocument;
    }

    public LocalDateTime getDeletedFromStorageAt() {
        return deletedFromStorageAt;
    }

    public void setDeletedFromStorageAt(LocalDateTime deletedFromStorageAt) {
        this.deletedFromStorageAt = deletedFromStorageAt;
    }

    public String getDeletionReason() {
        return deletionReason;
    }

    public void setDeletionReason(String deletionReason) {
        this.deletionReason = deletionReason;
    }
}