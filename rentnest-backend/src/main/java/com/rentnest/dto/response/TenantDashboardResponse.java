package com.rentnest.dto.response;

import java.util.List;

public record TenantDashboardResponse(
        long savedPropertiesCount,
        long pendingEnquiriesCount,
        long acceptedEnquiriesCount,
        long declinedEnquiriesCount,
        List<PropertyResponse> recentlyViewed
) {}
