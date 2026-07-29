package com.rentnest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Tenant dashboard statistics and navigation summary")
public record TenantDashboardResponse(
        @Schema(description = "Total number of properties saved to favourites", example = "2")
        long savedPropertiesCount,

        @Schema(description = "Total number of pending enquiries submitted by the tenant", example = "1")
        long pendingEnquiriesCount,

        @Schema(description = "Total number of accepted enquiries (revealing contact info)", example = "1")
        long acceptedEnquiriesCount,

        @Schema(description = "Total number of declined enquiries", example = "0")
        long declinedEnquiriesCount,

        @Schema(description = "List of up to 5 recently viewed properties (ordered by visitation timestamp)")
        List<PropertyResponse> recentlyViewed
) {}
