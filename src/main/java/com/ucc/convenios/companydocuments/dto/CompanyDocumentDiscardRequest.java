package com.ucc.convenios.companydocuments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyDocumentDiscardRequest {

    @NotBlank(message = "El motivo para descartar es obligatorio")
    @Size(max = 1000, message = "El motivo no puede superar 1000 caracteres")
    private String comment;

    public CompanyDocumentDiscardRequest() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}