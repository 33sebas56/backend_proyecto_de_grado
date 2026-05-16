package com.ucc.convenios.dashboard.dto;

import java.util.Map;

public record DashboardSummaryResponse(
        long totalUsers,
        long activeUsers,
        long totalCompanies,
        long companiesPendingValidation,
        long totalConvenios,
        Map<String, Long> conveniosByStatus,
        long conveniosApprovedForSignature,
        long conveniosPendingFormalization,
        long conveniosFormalized,
        long pendingApprovals,
        long activeAlerts
) {
}