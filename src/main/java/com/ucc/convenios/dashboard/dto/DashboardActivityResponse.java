package com.ucc.convenios.dashboard.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardActivityResponse(
        String type,
        String title,
        String description,
        UUID convenioId,
        String convenioCode,
        String performedBy,
        LocalDateTime createdAt
) {
}