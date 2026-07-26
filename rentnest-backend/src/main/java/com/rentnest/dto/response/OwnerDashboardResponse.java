package com.rentnest.dto.response;

public record OwnerDashboardResponse(
        long totalProperties,
        long activeListings,
        long rentedCount,
        long totalEnquiriesReceived
) {}
