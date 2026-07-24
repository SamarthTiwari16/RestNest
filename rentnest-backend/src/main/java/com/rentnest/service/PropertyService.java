package com.rentnest.service;

import com.rentnest.dto.request.PropertyRequest;
import com.rentnest.dto.response.PropertyResponse;
import java.util.List;

public interface PropertyService {
    PropertyResponse createProperty(PropertyRequest request, String ownerEmail);
    PropertyResponse updateProperty(Long id, PropertyRequest request, String ownerEmail);
    PropertyResponse getPropertyById(Long id, String currentUserEmail);
    List<PropertyResponse> getMyProperties(String ownerEmail);
    PropertyResponse submitForVerification(Long id, String ownerEmail);
    PropertyResponse markAsRented(Long id, String ownerEmail);
    PropertyResponse withdrawProperty(Long id, String ownerEmail);
}
