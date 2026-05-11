package com.ucc.convenios.companydocuments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyDocumentCorrectionRequest {

    @NotBlank(message = "El comentario de corrección es obligatorio")
    @Size(max = 1000, message = "El comentario no puede superar 1000 caracteres")
    private String comment;

    public CompanyDocumentCorrectionRequest() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}