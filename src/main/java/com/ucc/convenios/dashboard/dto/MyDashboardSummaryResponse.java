package com.ucc.convenios.dashboard.dto;

public record MyDashboardSummaryResponse(
        long myPendingApprovals,
        long myAlerts,
        long myUnreadAlerts,
        long myActiveCases,
        long myCreatedConvenios,
        long myConveniosInCorrection,
        long myConveniosPendingCompanyDocuments,
        long myConveniosReadyToSubmit
) {
}