package com.rentnest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectionRequest(
        @NotBlank(message = "Rejection reason is required")
        String reason
) {}
