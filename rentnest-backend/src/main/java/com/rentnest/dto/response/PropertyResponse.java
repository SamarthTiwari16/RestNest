package com.rentnest.dto.response;

import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "Detailed property listing response details")
public record PropertyResponse(
        @Schema(description = "Database ID of the property listing", example = "9")
        Long id,

        @Schema(description = "Owner profile who published the listing")
        UserResponse owner,

        @Schema(description = "Title of the listing", example = "Moderation Test House")
        String title,

        @Schema(description = "City where property is located", example = "Chennai")
        String city,

        @Schema(description = "Locality where property is located", example = "Velachery")
        String locality,

        @Schema(description = "Monthly rent amount in ₹", example = "30000.00")
        BigDecimal rent,

        @Schema(description = "BHK count", example = "3")
        Integer bhk,

        @Schema(description = "Property type designation", example = "APARTMENT")
        String propertyType,

        @Schema(description = "Furnishing status flag", example = "true")
        Boolean furnished,

        @Schema(description = "Pet allowance status flag", example = "true")
        Boolean petFriendly,

        @Schema(description = "Parking slot availability flag", example = "true")
        Boolean parking,

        @Schema(description = "Available starting from date", example = "2026-08-01")
        LocalDate availableFrom,

        @Schema(description = "Current lifecycle status of property listing", example = "ACTIVE")
        PropertyStatus status,

        @Schema(description = "Listing creation timestamp", example = "2026-07-29T16:35:00Z")
        Instant createdAt,

        @Schema(description = "Images uploaded for the property")
        List<PropertyImageResponse> images,

        @Schema(description = "Admin verification rejection feedback comment", example = "Please add clearer photos of the bedrooms.")
        String rejectionReason
) {
    @Schema(description = "Property image metadata response details")
    public record PropertyImageResponse(
            @Schema(description = "Database ID of the image record", example = "101")
            Long id,
            @Schema(description = "Web access URL for the image", example = "/uploads/photo.webp")
            String imageUrl,
            @Schema(description = "Display sorting order (0 is the cover image)", example = "0")
            Integer sortOrder
    ) {}

    public static PropertyResponse from(Property property) {
        List<PropertyImageResponse> imageResponses = property.getImages().stream()
                .map(img -> new PropertyImageResponse(img.getId(), img.getImageUrl(), img.getSortOrder()))
                .collect(Collectors.toList());

        return new PropertyResponse(
                property.getId(),
                UserResponse.from(property.getOwner()),
                property.getTitle(),
                property.getCity(),
                property.getLocality(),
                property.getRent(),
                property.getBhk(),
                property.getPropertyType(),
                property.getFurnished(),
                property.getPetFriendly(),
                property.getParking(),
                property.getAvailableFrom(),
                property.getStatus(),
                property.getCreatedAt(),
                imageResponses,
                property.getRejectionReason()
        );
    }
}
