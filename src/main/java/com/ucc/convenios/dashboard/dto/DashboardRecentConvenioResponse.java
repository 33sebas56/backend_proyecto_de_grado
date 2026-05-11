package com.ucc.convenios.dashboard.dto;

import com.ucc.convenios.convenios.entity.Convenio;

import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardRecentConvenioResponse(
        UUID convenioId,
        String convenioCode,
        String companyName,
        String status,
        String stage,
        String convenioType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DashboardRecentConvenioResponse fromEntity(Convenio convenio) {
        return new DashboardRecentConvenioResponse(
                convenio.getId(),
                convenio.getCode(),
                convenio.getCompany().getBusinessName(),
                convenio.getCurrentStatus().name(),
                convenio.getCurrentStage() == null ? null : convenio.getCurrentStage().name(),
                convenio.getConvenioType().name(),
                convenio.getCreatedAt(),
                convenio.getUpdatedAt()
        );
    }
}