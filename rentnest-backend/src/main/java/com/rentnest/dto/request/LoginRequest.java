package com.rentnest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User login credentials payload")
public record LoginRequest(
        @Schema(description = "Registered account email address", example = "testuser@example.com", maxLength = 254)
        @NotBlank @Email @Size(max = 254) String email,

        @Schema(description = "Account password", example = "Password123!", maxLength = 72)
        @NotBlank @Size(max = 72) String password
) {
}
