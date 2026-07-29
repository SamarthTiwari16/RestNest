package com.rentnest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Authentication response containing JWT token and user profile")
public record AuthResponse(
        @Schema(description = "JWT access token for authorized requests", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,

        @Schema(description = "Token type prefix", example = "Bearer")
        String tokenType,

        @Schema(description = "Timestamp when the token expires", example = "2026-07-29T17:15:00Z")
        Instant expiresAt,

        @Schema(description = "User profile information details")
        UserResponse user
) {
}
