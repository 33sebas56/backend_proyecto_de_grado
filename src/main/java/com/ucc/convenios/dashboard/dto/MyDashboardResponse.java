package com.ucc.convenios.dashboard.dto;

import java.util.List;

public record MyDashboardResponse(
        DashboardUserResponse user,
        List<ReviewerProfileDashboardResponse> reviewerProfiles,
        MyDashboardSummaryResponse summary
) {
}