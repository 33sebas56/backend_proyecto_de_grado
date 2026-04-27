package com.ucc.convenios.companies.dto;

import jakarta.validation.constraints.Size;

public class CompanyValidationRequest {

    @Size(max = 2000, message = "El comentario no puede superar 2000 caracteres")
    private String comment;

    public CompanyValidationRequest() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}