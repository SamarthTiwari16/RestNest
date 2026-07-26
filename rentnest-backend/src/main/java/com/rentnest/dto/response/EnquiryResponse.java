package com.rentnest.dto.response;

import com.rentnest.entity.Enquiry;
import com.rentnest.entity.EnquiryStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record EnquiryResponse(
        Long id,
        PropertySummary property,
        UserSummary tenant,
        UserSummary owner,
        String message,
        LocalDate moveInDate,
        Integer occupants,
        EnquiryStatus status,
        Instant createdAt
) {
    public record PropertySummary(
            Long id,
            String title,
            String city,
            String locality,
            java.math.BigDecimal rent,
            String propertyType,
            Integer bhk,
            Boolean furnished,
            Boolean parking,
            Boolean petFriendly,
            List<PropertyResponse.PropertyImageResponse> images
    ) {}

    public record UserSummary(
            Long id,
            String name,
            String email,
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
