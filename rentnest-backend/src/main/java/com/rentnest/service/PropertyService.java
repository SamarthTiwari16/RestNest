package com.rentnest.service;

import com.rentnest.dto.request.PropertyRequest;
import com.rentnest.dto.response.PropertyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PropertyService {
    PropertyResponse createProperty(PropertyRequest request, String ownerEmail);
    PropertyResponse updateProperty(Long id, PropertyRequest request, String ownerEmail);
    PropertyResponse getPropertyById(Long id, String currentUserEmail);
    List<PropertyResponse> getMyProperties(String ownerEmail);
    PropertyResponse submitForVerification(Long id, String ownerEmail);
    PropertyResponse markAsRented(Long id, String ownerEmail);
    PropertyResponse withdrawProperty(Long id, String ownerEmail);
    Page<PropertyResponse> searchProperties(
            String city,
            String locality,
            BigDecimal minRent,
            BigDecimal maxRent,
            Integer bhk,
            Boolean furnished,
            Boolean parking,
            Boolean petFriendly,
            LocalDate availableFrom,
            String propertyType,
            Pageable pageable
    );
    Page<PropertyResponse> getPendingProperties(Pageable pageable);
    PropertyResponse approveProperty(Long id);
    PropertyResponse rejectProperty(Long id, String reason);
    PropertyResponse deactivateProperty(Long id);
}
