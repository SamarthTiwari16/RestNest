package com.rentnest.controller;

import com.rentnest.dto.response.OwnerDashboardResponse;
import com.rentnest.dto.response.TenantDashboardResponse;
import com.rentnest.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/owner")
    @Operation(summary = "Get owner dashboard statistics")
    public ResponseEntity<OwnerDashboardResponse> getOwnerDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getOwnerDashboard(userDetails.getUsername()));
    }

    @GetMapping("/tenant")
    @Operation(summary = "Get tenant dashboard statistics")
    public ResponseEntity<TenantDashboardResponse> getTenantDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.getTenantDashboard(userDetails.getUsername()));
    }
}
