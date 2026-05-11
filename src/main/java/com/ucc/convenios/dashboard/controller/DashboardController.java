package com.ucc.convenios.dashboard.controller;

import com.ucc.convenios.dashboard.dto.DashboardActivityResponse;
import com.ucc.convenios.dashboard.dto.DashboardSummaryResponse;
import com.ucc.convenios.dashboard.dto.DashboardWorkResponse;
import com.ucc.convenios.dashboard.dto.MyDashboardResponse;
import com.ucc.convenios.dashboard.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/me")
    public MyDashboardResponse getMyDashboard(Authentication authentication) {
        return dashboardService.getMyDashboard(authentication);
    }

    @GetMapping("/my-work")
    public DashboardWorkResponse getMyWork(Authentication authentication) {
        return dashboardService.getMyWork(authentication);
    }

    @GetMapping("/admin/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardSummaryResponse getAdminSummary() {
        return dashboardService.getAdminSummary();
    }

    @GetMapping("/recent-activity")
    public List<DashboardActivityResponse> getRecentActivity() {
        return dashboardService.getRecentActivity();
    }
}