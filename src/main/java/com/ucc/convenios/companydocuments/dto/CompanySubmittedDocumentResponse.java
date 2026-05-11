package com.ucc.convenios.companydocuments.dto;

import com.ucc.convenios.companydocuments.entity.CompanySubmittedDocument;

import java.time.LocalDateTime;
import java.util.UUID;

public class CompanySubmittedDocumentResponse {

    private UUID id;
    private UUID requestId;
    private UUID convenioId;
    private String documentType;
    private String displayName;
    private String originalFilename;
    private String mimeType;
    private Long fileSize;
    private String status;
    private String reviewComment;
    private LocalDateTime uploadedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime reviewedAt;
    private UUID reviewedById;
    private String reviewedByName;
    private UUID replacedByDocumentId;
    private LocalDateTime deletedFromStorageAt;
    private String deletionReason;

    public CompanySubmittedDocumentResponse() {
    }

    public static CompanySubmittedDocumentResponse fromEntity(CompanySubmittedDocument document) {
        CompanySubmittedDocumentResponse response = new CompanySubmittedDocumentResponse();
        response.setId(document.getId());
        response.setRequestId(document.getRequest().getId());
        response.setConvenioId(document.getConvenio().getId());
        response.setDocumentType(document.getDocumentType().name());
        response.setDisplayName(document.getDisplayName());
        response.setOriginalFilename(document.getOriginalFilename());
        response.setMimeType(document.getMimeType());
        response.setFileSize(document.getFileSize());
        response.setStatus(document.getStatus().name());
        response.setReviewComment(document.getReviewComment());
        response.setUploadedAt(document.getUploadedAt());
        response.setApprovedAt(document.getApprovedAt());
        response.setReviewedAt(document.getReviewedAt());
        response.setDeletedFromStorageAt(document.getDeletedFromStorageAt());
        response.setDeletionReason(document.getDeletionReason());

        if (document.getReviewedBy() != null) {
            response.setReviewedById(document.getReviewedBy().getId());
            response.setReviewedByName(document.getReviewedBy().getFullName());
        }

        if (document.getReplacedByDocument() != null) {
            response.setReplacedByDocumentId(document.getReplacedByDocument().getId());
        }

        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getConvenioId() {
        return convenioId;
    }

    public void setConvenioId(UUID convenioId) {
        this.convenioId = convenioId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public UUID getReviewedById() {
        return reviewedById;
    }

    public void setReviewedById(UUID reviewedById) {
        this.reviewedById = reviewedById;
    }

    public String getReviewedByName() {
        return reviewedByName;
    }

    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
    }

    public UUID getReplacedByDocumentId() {
        return replacedByDocumentId;
    }

    public void setReplacedByDocumentId(UUID replacedByDocumentId) {
        this.replacedByDocumentId = replacedByDocumentId;
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