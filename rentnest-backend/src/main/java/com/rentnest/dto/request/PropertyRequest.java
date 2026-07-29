package com.rentnest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Property creation or update payload")
public record PropertyRequest(
        @Schema(description = "Listing title of the property", example = "Spacious 2BHK flat with balcony", maxLength = 150)
        @NotBlank @Size(max = 150) String title,

        @Schema(description = "City where the property is located", example = "Chennai", maxLength = 100)
        @NotBlank @Size(max = 100) String city,

        @Schema(description = "Specific locality or neighborhood", example = "Velachery", maxLength = 100)
        @NotBlank @Size(max = 100) String locality,

        @Schema(description = "Monthly rent amount in ₹", example = "30000.00")
        @NotNull @DecimalMin(value = "0.01", message = "must be greater than 0") BigDecimal rent,

        @Schema(description = "BHK (Bedroom, Hall, Kitchen) configuration count", example = "3")
        @NotNull @Min(value = 1, message = "must be at least 1") Integer bhk,

        @Schema(description = "Type of the property (e.g., APARTMENT, VILLA, STUDIO, INDEPENDENT_HOUSE)", example = "APARTMENT", maxLength = 50)
        @NotBlank @Size(max = 50) String propertyType,

        @Schema(description = "Indicates if the property is fully furnished", example = "true")
        @NotNull Boolean furnished,

        @Schema(description = "Indicates if pets are allowed in the property", example = "true")
        @NotNull Boolean petFriendly,

        @Schema(description = "Indicates if dedicated parking is available", example = "true")
        @NotNull Boolean parking,

        @Schema(description = "Date when the property will be available for occupancy", example = "2026-08-01")
        @NotNull LocalDate availableFrom,

        @Schema(description = "List of relative image URLs uploaded for the listing (at least 1 is required for verification)", example = "[\"/uploads/photo1.webp\"]")
        List<String> imageUrls
) {}
