package com.rentnest.service;

import com.rentnest.dto.response.OwnerDashboardResponse;
import com.rentnest.dto.response.TenantDashboardResponse;

public interface DashboardService {
    OwnerDashboardResponse getOwnerDashboard(String ownerEmail);
    TenantDashboardResponse getTenantDashboard(String tenantEmail);
    void logPropertyView(Long propertyId, String userEmail);
}
