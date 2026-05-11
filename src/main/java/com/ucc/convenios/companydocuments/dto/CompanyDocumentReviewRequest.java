package com.ucc.convenios.companydocuments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyDocumentReviewRequest {

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 1000, message = "El comentario no puede superar 1000 caracteres")
    private String comment;

    private boolean deletePhysicalFile = true;

    public CompanyDocumentReviewRequest() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDeletePhysicalFile() {
        return deletePhysicalFile;
    }

    public void setDeletePhysicalFile(boolean deletePhysicalFile) {
        this.deletePhysicalFile = deletePhysicalFile;
    }
}