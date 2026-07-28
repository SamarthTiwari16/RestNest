package com.rentnest.service.impl;

import com.rentnest.dto.request.PropertyRequest;
import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyImage;
import com.rentnest.entity.PropertyStatus;
import com.rentnest.entity.User;
import com.rentnest.entity.Role;
import com.rentnest.exception.ResourceNotFoundException;
import com.rentnest.exception.UnauthorizedActionException;
import com.rentnest.exception.ValidationException;
import com.rentnest.repository.PropertyRepository;
import com.rentnest.repository.UserRepository;
import com.rentnest.service.PropertyService;
import com.rentnest.service.ImageStorageService;
import com.rentnest.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.rentnest.repository.specification.PropertySpecification;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;
    private DashboardService dashboardService;

    public PropertyServiceImpl(PropertyRepository propertyRepository, UserRepository userRepository, ImageStorageService imageStorageService) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setDashboardService(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    public PropertyResponse createProperty(PropertyRequest request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerEmail));

        Property property = new Property(
                owner,
                request.title().trim(),
                request.city().trim(),
                request.locality().trim(),
                request.rent(),
                request.bhk(),
                request.propertyType().trim(),
                request.furnished(),
                request.petFriendly(),
                request.parking(),
                request.availableFrom(),
                PropertyStatus.DRAFT
        );

        if (request.imageUrls() != null) {
            for (int i = 0; i < request.imageUrls().size(); i++) {
                property.getImages().add(new PropertyImage(property, request.imageUrls().get(i), i));
            }
        }

        Property savedProperty = propertyRepository.save(property);
        log.info("Property created: propertyId={}, ownerId={}", savedProperty.getId(), owner.getId());
        return PropertyResponse.from(savedProperty);
    }

    @Override
    public PropertyResponse updateProperty(Long id, PropertyRequest request, String ownerEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        verifyOwnership(property, ownerEmail);

        if (property.getStatus() != PropertyStatus.DRAFT) {
            throw new ValidationException("Property can only be edited while in DRAFT status");
        }

        property.setTitle(request.title().trim());
        property.setCity(request.city().trim());
        property.setLocality(request.locality().trim());
        property.setRent(request.rent());
        property.setBhk(request.bhk());
        property.setPropertyType(request.propertyType().trim());
        property.setFurnished(request.furnished());
        property.setPetFriendly(request.petFriendly());
        property.setParking(request.parking());
        property.setAvailableFrom(request.availableFrom());

        // Update images list and clean up disk orphans
        if (request.imageUrls() != null) {
            Set<String> newUrls = new HashSet<>(request.imageUrls());
            for (PropertyImage existingImage : property.getImages()) {
                if (!newUrls.contains(existingImage.getImageUrl())) {
                    imageStorageService.deleteImage(existingImage.getImageUrl());
                }
            }
            property.getImages().clear();
            for (int i = 0; i < request.imageUrls().size(); i++) {
                property.getImages().add(new PropertyImage(property, request.imageUrls().get(i), i));
            }
        }

        Property updatedProperty = propertyRepository.save(property);
        log.info("Property updated: propertyId={}", updatedProperty.getId());
        return PropertyResponse.from(updatedProperty);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyResponse getPropertyById(Long id, String currentUserEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        // If the property is in DRAFT or PENDING_VERIFICATION, only allow the owner or an admin to read it
        if (property.getStatus() == PropertyStatus.DRAFT || property.getStatus() == PropertyStatus.PENDING_VERIFICATION) {
            if (currentUserEmail == null) {
                throw new UnauthorizedActionException("You are not authorized to view this listing");
            }
            User user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserEmail));
            
            boolean isOwner = property.getOwner().getId().equals(user.getId());
            boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

            if (!isOwner && !isAdmin) {
                throw new UnauthorizedActionException("You are not authorized to view this listing");
            }
        }

        // Log view for recently-viewed tracking (async-safe: ignore errors)
        if (dashboardService != null && currentUserEmail != null) {
            try {
                dashboardService.logPropertyView(id, currentUserEmail);
            } catch (Exception e) {
                log.warn("Failed to log property view: propertyId={}, user={}", id, currentUserEmail);
            }
        }

        return PropertyResponse.from(property);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponse> getMyProperties(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerEmail));

        return propertyRepository.findAllByOwnerId(owner.getId()).stream()
                .map(PropertyResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyResponse submitForVerification(Long id, String ownerEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        verifyOwnership(property, ownerEmail);

        if (property.getStatus() != PropertyStatus.DRAFT) {
            throw new ValidationException("Only DRAFT properties can be submitted for verification");
        }

        if (property.getImages().isEmpty()) {
            throw new ValidationException("Property must have at least one image before submission");
        }

        property.setStatus(PropertyStatus.PENDING_VERIFICATION);
        Property updatedProperty = propertyRepository.save(property);
        log.info("Property submitted for verification: propertyId={}", updatedProperty.getId());
        return PropertyResponse.from(updatedProperty);
    }

    @Override
    public PropertyResponse markAsRented(Long id, String ownerEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        verifyOwnership(property, ownerEmail);

        if (property.getStatus() != PropertyStatus.ACTIVE && property.getStatus() != PropertyStatus.APPROVED) {
            throw new ValidationException("Property must be ACTIVE or APPROVED to be marked as rented");
        }

        property.setStatus(PropertyStatus.RENTED);
        Property updatedProperty = propertyRepository.save(property);
        log.info("Property marked as rented: propertyId={}", updatedProperty.getId());
        return PropertyResponse.from(updatedProperty);
    }

    @Override
    public PropertyResponse withdrawProperty(Long id, String ownerEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        verifyOwnership(property, ownerEmail);

        if (property.getStatus() != PropertyStatus.ACTIVE && property.getStatus() != PropertyStatus.APPROVED) {
            throw new ValidationException("Property must be ACTIVE or APPROVED to be withdrawn");
        }

        property.setStatus(PropertyStatus.ARCHIVED);
        Property updatedProperty = propertyRepository.save(property);
        log.info("Property withdrawn: propertyId={}", updatedProperty.getId());
        return PropertyResponse.from(updatedProperty);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyResponse> searchProperties(
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
    ) {
        Specification<Property> spec = PropertySpecification.filterProperties(
                city, locality, minRent, maxRent, bhk, furnished, parking, petFriendly, availableFrom, propertyType
        );
        return propertyRepository.findAll(spec, pageable).map(PropertyResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyResponse> getPendingProperties(Pageable pageable) {
        return propertyRepository.findAllByStatus(PropertyStatus.PENDING_VERIFICATION, pageable)
                .map(PropertyResponse::from);
    }

    @Override
    public PropertyResponse approveProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        if (property.getStatus() != PropertyStatus.PENDING_VERIFICATION) {
            throw new ValidationException("Property is not in PENDING_VERIFICATION status");
        }
        property.setStatus(PropertyStatus.ACTIVE);
        property.setRejectionReason(null);
        Property saved = propertyRepository.save(property);
        log.info("Property approved: propertyId={}", saved.getId());
        return PropertyResponse.from(saved);
    }

    @Override
    public PropertyResponse rejectProperty(Long id, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Rejection reason is required");
        }
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        if (property.getStatus() != PropertyStatus.PENDING_VERIFICATION) {
            throw new ValidationException("Property is not in PENDING_VERIFICATION status");
        }
        property.setStatus(PropertyStatus.DRAFT);
        property.setRejectionReason(reason.trim());
        Property saved = propertyRepository.save(property);
        log.info("Property rejected: propertyId={}, reason={}", saved.getId(), reason);
        return PropertyResponse.from(saved);
    }

    @Override
    public PropertyResponse deactivateProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        if (property.getStatus() != PropertyStatus.ACTIVE) {
            throw new ValidationException("Only ACTIVE properties can be deactivated by admin");
        }
        property.setStatus(PropertyStatus.ARCHIVED);
        Property saved = propertyRepository.save(property);
        log.info("Property deactivated by admin: propertyId={}", saved.getId());
        return PropertyResponse.from(saved);
    }

    private void verifyOwnership(Property property, String ownerEmail) {
        User user = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerEmail));

        if (!property.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You do not own this property");
        }
    }
}
