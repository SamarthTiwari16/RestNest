package com.rentnest.dto.response;

import com.rentnest.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "User profile response details")
public record UserResponse(
        @Schema(description = "Database ID of the user", example = "10")
        Long id,

        @Schema(description = "Full name of the user", example = "Priya Shah")
        String name,

        @Schema(description = "Email address of the user", example = "priya@example.com")
        String email,

        @Schema(description = "Indian phone number of the user", example = "+919876543210")
        String phone,

        @Schema(description = "Account security role (e.g. ROLE_USER, ROLE_ADMIN)", example = "ROLE_USER")
        String role,

        @Schema(description = "Account creation timestamp", example = "2026-07-29T16:30:00Z")
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole().name(), user.getCreatedAt());
    }
}
