package com.ucc.convenios.dashboard.dto;

import com.ucc.convenios.notifications.dto.ReviewAlertResponse;

import java.util.List;

public record DashboardWorkResponse(
        List<DashboardPendingApprovalResponse> pendingApprovals,
        List<ReviewAlertResponse> alerts,
        List<DashboardRecentConvenioResponse> recentCreatedConvenios
) {
}