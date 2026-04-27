package com.ucc.convenios.convenios.dto;

import com.ucc.convenios.convenios.entity.ConvenioGeneratedDocument;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConvenioGeneratedDocumentResponse {

    private UUID id;
    private UUID convenioId;
    private UUID convenioVersionId;
    private UUID approvalStepId;
    private String documentType;
    private String stage;
    private String fileName;
    private String url;
    private UUID generatedById;
    private String generatedByEmail;
    private LocalDateTime generatedAt;
    private String notes;

    public ConvenioGeneratedDocumentResponse() {
    }

    public static ConvenioGeneratedDocumentResponse fromEntity(ConvenioGeneratedDocument document) {
        ConvenioGeneratedDocumentResponse response = new ConvenioGeneratedDocumentResponse();
        response.setId(document.getId());
        response.setConvenioId(document.getConvenio().getId());
        response.setConvenioVersionId(document.getConvenioVersion().getId());
        response.setDocumentType(document.getDocumentType().name());
        response.setFileName(document.getFileName());
        response.setUrl(document.getUrl());
        response.setGeneratedById(document.getGeneratedBy().getId());
        response.setGeneratedByEmail(document.getGeneratedBy().getEmail());
        response.setGeneratedAt(document.getGeneratedAt());
        response.setNotes(document.getNotes());

        if (document.getApprovalStep() != null) {
            response.setApprovalStepId(document.getApprovalStep().getId());
        }

        if (document.getStage() != null) {
            response.setStage(document.getStage().name());
        }

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

    public UUID getConvenioVersionId() {
        return convenioVersionId;
    }

    public void setConvenioVersionId(UUID convenioVersionId) {
        this.convenioVersionId = convenioVersionId;
    }

    public UUID getApprovalStepId() {
        return approvalStepId;
    }

    public void setApprovalStepId(UUID approvalStepId) {
        this.approvalStepId = approvalStepId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getGeneratedById() {
        return generatedById;
    }

    public void setGeneratedById(UUID generatedById) {
        this.generatedById = generatedById;
    }

    public String getGeneratedByEmail() {
        return generatedByEmail;
    }

    public void setGeneratedByEmail(String generatedByEmail) {
        this.generatedByEmail = generatedByEmail;
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