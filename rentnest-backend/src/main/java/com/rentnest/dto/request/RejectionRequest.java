package com.rentnest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Admin property listing rejection payload")
public record RejectionRequest(
        @Schema(description = "Required reason explaining why the listing is being rejected", example = "Please add clearer photos of the bedrooms.")
        @NotBlank(message = "Rejection reason is required")
        String reason
) {}
