package com.rentnest.dto.response;

import com.rentnest.entity.Enquiry;
import com.rentnest.entity.EnquiryStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Schema(description = "Enquiry record response details")
public record EnquiryResponse(
        @Schema(description = "Database ID of the enquiry", example = "100")
        Long id,

        @Schema(description = "Summary details of the enquired property")
        PropertySummary property,

        @Schema(description = "Summary profile of the tenant sending enquiry")
        UserSummary tenant,

        @Schema(description = "Summary profile of the property owner (masked initially unless accepted)")
        UserSummary owner,

        @Schema(description = "Introduction message from the tenant", example = "Hi, I am interested in this flat.")
        String message,

        @Schema(description = "Intended move-in date", example = "2026-08-01")
        LocalDate moveInDate,

        @Schema(description = "Number of occupants", example = "2")
        Integer occupants,

        @Schema(description = "Current lifecycle status of the enquiry", example = "PENDING")
        EnquiryStatus status,

        @Schema(description = "Enquiry submission timestamp", example = "2026-07-29T16:36:00Z")
        Instant createdAt
) {
    @Schema(description = "Summary property info for enquiry context")
    public record PropertySummary(
            @Schema(description = "Database ID of the property", example = "9")
            Long id,
            @Schema(description = "Property title", example = "Moderation Test House")
            String title,
            @Schema(description = "Property city", example = "Chennai")
            String city,
            @Schema(description = "Property locality", example = "Velachery")
            String locality,
            @Schema(description = "Monthly rent in ₹", example = "30000.00")
            java.math.BigDecimal rent,
            @Schema(description = "Property type", example = "APARTMENT")
            String propertyType,
            @Schema(description = "BHK count", example = "3")
            Integer bhk,
            @Schema(description = "Furnished flag", example = "true")
            Boolean furnished,
            @Schema(description = "Parking slot flag", example = "true")
            Boolean parking,
            @Schema(description = "Pet friendly flag", example = "true")
            Boolean petFriendly,
            @Schema(description = "List of property images")
            List<PropertyResponse.PropertyImageResponse> images
    ) {}

    @Schema(description = "Summary user contact details")
    public record UserSummary(
            @Schema(description = "Database ID of the user", example = "10")
            Long id,
            @Schema(description = "User display name", example = "Priya Shah")
            String name,
            @Schema(description = "User contact email (may be null/masked if unauthorized)", example = "priya@example.com")
            String email,
            @Schema(description = "User contact phone number (may be null/masked if unauthorized)", example = "+919876543210")
            String phone
    ) {}

    public static EnquiryResponse from(Enquiry enquiry, boolean showContactInfo) {
        var prop = enquiry.getProperty();
        List<PropertyResponse.PropertyImageResponse> imagesResponse = prop.getImages() != null ?
                prop.getImages().stream()
                        .map(img -> new PropertyResponse.PropertyImageResponse(img.getId(), img.getImageUrl(), img.getSortOrder()))
                        .toList() :
                Collections.emptyList();

        PropertySummary propertySummary = new PropertySummary(
                prop.getId(),
                prop.getTitle(),
                prop.getCity(),
                prop.getLocality(),
                prop.getRent(),
                prop.getPropertyType(),
                prop.getBhk(),
                prop.getFurnished(),
                prop.getParking(),
                prop.getPetFriendly(),
                imagesResponse
        );

        UserSummary tenantSummary = new UserSummary(
                enquiry.getTenant().getId(),
                enquiry.getTenant().getName(),
                enquiry.getTenant().getEmail(),
                enquiry.getTenant().getPhone()
        );

        UserSummary ownerSummary = new UserSummary(
                enquiry.getOwner().getId(),
                enquiry.getOwner().getName(),
                showContactInfo ? enquiry.getOwner().getEmail() : null,
                showContactInfo ? enquiry.getOwner().getPhone() : null
        );

        return new EnquiryResponse(
                enquiry.getId(),
                propertySummary,
                tenantSummary,
                ownerSummary,
                enquiry.getMessage(),
                enquiry.getMoveInDate(),
                enquiry.getOccupants(),
                enquiry.getStatus(),
                enquiry.getCreatedAt()
        );
    }
}
