package com.rentnest.dto.response;

import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

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
        Instant createdAt
) {
    public static PropertyResponse from(Property property) {
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
                property.getCreatedAt()
        );
    }
}
