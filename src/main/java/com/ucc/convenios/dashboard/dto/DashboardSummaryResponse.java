package com.ucc.convenios.dashboard.dto;

import java.util.Map;

public record DashboardSummaryResponse(
        long totalUsers,
        long activeUsers,
        long totalCompanies,
        long companiesPendingValidation,
        long totalConvenios,
        Map<String, Long> conveniosByStatus,
        long pendingApprovals,
        long activeAlerts
) {
}