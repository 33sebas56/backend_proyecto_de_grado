package com.ucc.convenios.dashboard.dto;

import com.ucc.convenios.approvals.entity.ApprovalStep;

import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardPendingApprovalResponse(
        UUID stepId,
        UUID convenioId,
        String convenioCode,
        String convenioTitle,
        String companyName,
        String stage,
        LocalDateTime assignedAt,
        LocalDateTime dueAt
) {
    public static DashboardPendingApprovalResponse fromEntity(ApprovalStep step) {
        return new DashboardPendingApprovalResponse(
                step.getId(),
                step.getApprovalRound().getConvenio().getId(),
                step.getApprovalRound().getConvenio().getCode(),
                step.getApprovalRound().getConvenioVersion().getTitle(),
                step.getApprovalRound().getConvenio().getCompany().getBusinessName(),
                step.getStage().name(),
                step.getAssignedAt(),
                step.getDueAt()
        );
    }
}