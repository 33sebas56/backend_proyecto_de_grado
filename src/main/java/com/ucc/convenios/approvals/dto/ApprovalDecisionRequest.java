package com.ucc.convenios.approvals.dto;

import jakarta.validation.constraints.Size;

public class ApprovalDecisionRequest {

    @Size(max = 2000, message = "El comentario no puede superar 2000 caracteres")
    private String comment;

    public ApprovalDecisionRequest() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}