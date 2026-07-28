package com.rentnest.dto.response;

import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record PropertyResponse(
        Long id,
        UserResponse owner,
        String title,
        String city,
        String locality,
        BigDecimal rent,
        Integer bhk,
        String propertyType,
        Boolean furnished,
        Boolean petFriendly,
        Boolean parking,
        LocalDate availableFrom,
        PropertyStatus status,
        Instant createdAt,
        List<PropertyImageResponse> images,
        String rejectionReason
) {
    public record PropertyImageResponse(Long id, String imageUrl, Integer sortOrder) {}

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
