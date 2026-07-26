package com.rentnest.service.impl;

import com.rentnest.dto.request.EnquiryRequest;
import com.rentnest.dto.response.EnquiryResponse;
import com.rentnest.entity.*;
import com.rentnest.exception.ResourceNotFoundException;
import com.rentnest.exception.UnauthorizedActionException;
import com.rentnest.exception.ValidationException;
import com.rentnest.repository.EnquiryRepository;
import com.rentnest.repository.PropertyRepository;
import com.rentnest.repository.UserRepository;
import com.rentnest.service.EnquiryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnquiryServiceImpl implements EnquiryService {

    private static final Logger log = LoggerFactory.getLogger(EnquiryServiceImpl.class);

    private final EnquiryRepository enquiryRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public EnquiryServiceImpl(EnquiryRepository enquiryRepository, UserRepository userRepository, PropertyRepository propertyRepository) {
        this.enquiryRepository = enquiryRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public EnquiryResponse sendEnquiry(Long propertyId, EnquiryRequest request, String tenantEmail) {
        User tenant = userRepository.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantEmail));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + propertyId));

        if (property.getStatus() != PropertyStatus.ACTIVE) {
            throw new ValidationException("Cannot enquire on a property that is not ACTIVE");
        }

        if (property.getOwner().getId().equals(tenant.getId())) {
            throw new ValidationException("You cannot submit an enquiry for your own property");
        }

        // Check for existing active (PENDING or ACCEPTED) enquiry
        boolean exists = enquiryRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                propertyId,
                tenant.getId(),
                List.of(EnquiryStatus.PENDING, EnquiryStatus.ACCEPTED)
        );
        if (exists) {
            throw new ValidationException("You already have an active enquiry for this property");
        }

        Enquiry enquiry = new Enquiry(
                property,
                tenant,
                property.getOwner(),
                request.message(),
                request.moveInDate(),
                request.occupants()
        );

        Enquiry savedEnquiry = enquiryRepository.save(enquiry);
        log.info("Enquiry submitted: enquiryId={}, propertyId={}, tenantId={}", savedEnquiry.getId(), propertyId, tenant.getId());

        // Contact info is masked initially since status is PENDING
        return EnquiryResponse.from(savedEnquiry, false);
    }

    @Override
    public EnquiryResponse acceptEnquiry(Long enquiryId, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerEmail));
        Enquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry not found: " + enquiryId));

        if (!enquiry.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You are not authorized to accept this enquiry");
        }

        if (enquiry.getStatus() != EnquiryStatus.PENDING) {
            throw new ValidationException("Enquiry is already " + enquiry.getStatus());
        }

        enquiry.setStatus(EnquiryStatus.ACCEPTED);
        Enquiry updatedEnquiry = enquiryRepository.save(enquiry);
        log.info("Enquiry accepted: enquiryId={}", enquiryId);

        // Show contact info since status is ACCEPTED
        return EnquiryResponse.from(updatedEnquiry, true);
    }

    @Override
    public EnquiryResponse declineEnquiry(Long enquiryId, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerEmail));
        Enquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry not found: " + enquiryId));

        if (!enquiry.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You are not authorized to decline this enquiry");
        }

        if (enquiry.getStatus() != EnquiryStatus.PENDING) {
            throw new ValidationException("Enquiry is already " + enquiry.getStatus());
        }

        enquiry.setStatus(EnquiryStatus.DECLINED);
        Enquiry updatedEnquiry = enquiryRepository.save(enquiry);
        log.info("Enquiry declined: enquiryId={}", enquiryId);

        // Mask contact info since status is DECLINED
        return EnquiryResponse.from(updatedEnquiry, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnquiryResponse> getSentEnquiries(String tenantEmail) {
        User tenant = userRepository.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + tenantEmail));

        return enquiryRepository.findAllByTenantId(tenant.getId())
                .stream()
                .map(e -> EnquiryResponse.from(e, e.getStatus() == EnquiryStatus.ACCEPTED))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnquiryResponse> getReceivedEnquiries(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerEmail));

        // Owner can always see tenant contact info
        return enquiryRepository.findAllByOwnerId(owner.getId())
                .stream()
                .map(e -> EnquiryResponse.from(e, true))
                .toList();
    }
}
