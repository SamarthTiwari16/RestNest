package com.rentnest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Owner dashboard statistics summary")
public record OwnerDashboardResponse(
        @Schema(description = "Total number of properties created by the owner", example = "5")
        long totalProperties,

        @Schema(description = "Total number of active property listings visible to tenants", example = "3")
        long activeListings,

        @Schema(description = "Total number of properties marked as RENTED", example = "1")
        long rentedCount,

        @Schema(description = "Total number of enquiries received on the owner's properties", example = "8")
        long totalEnquiriesReceived
) {}
