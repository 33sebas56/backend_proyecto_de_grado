package com.ucc.convenios.companydocuments.dto;

public class RequiredCompanyDocumentResponse {

    private String documentType;
    private String displayName;

    public RequiredCompanyDocumentResponse() {
    }

    public RequiredCompanyDocumentResponse(String documentType, String displayName) {
        this.documentType = documentType;
        this.displayName = displayName;
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
}