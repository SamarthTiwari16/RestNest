package com.rentnest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request payload")
public record RegisterRequest(
        @Schema(description = "Full name of the user", example = "Priya Shah", maxLength = 100)
        @NotBlank @Size(max = 100) String name,

        @Schema(description = "Unique email address for account login", example = "priya@example.com", maxLength = 254)
        @NotBlank @Email @Size(max = 254) String email,

        @Schema(description = "Indian phone number with optional +91 prefix", example = "+919876543210")
        @NotBlank @Pattern(regexp = "^(?:\\+91)?[6-9]\\d{9}$", message = "must be a valid Indian mobile number") String phone,

        @Schema(description = "Strong password containing uppercase, lowercase, digits, and special characters", example = "Password123!", minLength = 8, maxLength = 72)
        @NotBlank
        @Size(min = 8, max = 72)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$", message = "must contain uppercase, lowercase, number, and special character")
        String password
) {
}
