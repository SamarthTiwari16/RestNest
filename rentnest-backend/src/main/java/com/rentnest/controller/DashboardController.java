package com.rentnest.controller;

import com.rentnest.dto.response.OwnerDashboardResponse;
import com.rentnest.dto.response.TenantDashboardResponse;
import com.rentnest.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/owner")
    public ResponseEntity<OwnerDashboardResponse> getOwnerDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getOwnerDashboard(userDetails.getUsername()));
    }

    @GetMapping("/tenant")
    public ResponseEntity<TenantDashboardResponse> getTenantDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getTenantDashboard(userDetails.getUsername()));
    }
}
