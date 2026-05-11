package com.ucc.convenios.dashboard.dto;

import com.ucc.convenios.users.entity.ReviewerProfile;

import java.util.UUID;

public record ReviewerProfileDashboardResponse(
        UUID id,
        String roleName,
        Boolean available,
        Integer currentActiveCases,
        Integer maxActiveCases,
        String sealName
) {
    public static ReviewerProfileDashboardResponse fromEntity(ReviewerProfile profile) {
        return new ReviewerProfileDashboardResponse(
                profile.getId(),
                profile.getRole().getName(),
                profile.getAvailable(),
                profile.getCurrentActiveCases(),
                profile.getMaxActiveCases(),
                profile.getSealName()
        );
    }
}