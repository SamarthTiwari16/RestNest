package com.rentnest.service.impl;

import com.rentnest.dto.request.PropertyRequest;
import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyStatus;
import com.rentnest.entity.User;
import com.rentnest.entity.Role;
import com.rentnest.exception.ResourceNotFoundException;
import com.rentnest.exception.UnauthorizedActionException;
import com.rentnest.exception.ValidationException;
import com.rentnest.repository.PropertyRepository;
import com.rentnest.repository.UserRepository;
import com.rentnest.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository, UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
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

        // In early phases before moderation is built, properties will remain PENDING_VERIFICATION.
        // But to make the service defensive, we allow transitions from ACTIVE or APPROVED to RENTED.
        // Wait, for flexibility during Phase 2 testing, let's allow moving to RENTED from ACTIVE or APPROVED.
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

    private void verifyOwnership(Property property, String ownerEmail) {
        User user = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerEmail));

        if (!property.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You do not own this property");
        }
    }
}
