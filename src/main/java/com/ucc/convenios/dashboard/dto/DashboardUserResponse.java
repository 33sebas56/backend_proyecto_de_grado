package com.ucc.convenios.dashboard.dto;

import com.ucc.convenios.users.entity.User;

import java.util.List;
import java.util.UUID;

public record DashboardUserResponse(
        UUID id,
        String fullName,
        String email,
        List<String> roles
) {
    public static DashboardUserResponse fromEntity(User user, List<String> roles) {
        return new DashboardUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                roles
        );
    }
}