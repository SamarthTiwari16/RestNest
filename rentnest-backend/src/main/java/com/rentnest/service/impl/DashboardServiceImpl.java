package com.rentnest.service.impl;

import com.rentnest.dto.response.OwnerDashboardResponse;
import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.dto.response.TenantDashboardResponse;
import com.rentnest.entity.*;
import com.rentnest.exception.ResourceNotFoundException;
import com.rentnest.repository.*;
import com.rentnest.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final EnquiryRepository enquiryRepository;
    private final FavouriteRepository favouriteRepository;
    private final RecentlyViewedRepository recentlyViewedRepository;

    public DashboardServiceImpl(
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            EnquiryRepository enquiryRepository,
            FavouriteRepository favouriteRepository,
            RecentlyViewedRepository recentlyViewedRepository
    ) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.enquiryRepository = enquiryRepository;
        this.favouriteRepository = favouriteRepository;
        this.recentlyViewedRepository = recentlyViewedRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerDashboardResponse getOwnerDashboard(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerEmail));

        long totalProperties = propertyRepository.countByOwnerId(owner.getId());
        long activeListings = propertyRepository.countByOwnerIdAndStatus(owner.getId(), PropertyStatus.ACTIVE);
        long rentedCount = propertyRepository.countByOwnerIdAndStatus(owner.getId(), PropertyStatus.RENTED);
        long totalEnquiriesReceived = enquiryRepository.countByOwnerId(owner.getId());

        log.info("Owner dashboard fetched: ownerId={}", owner.getId());
        return new OwnerDashboardResponse(totalProperties, activeListings, rentedCount, totalEnquiriesReceived);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDashboardResponse getTenantDashboard(String tenantEmail) {
        User tenant = userRepository.findByEmail(tenantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + tenantEmail));

        long savedCount = favouriteRepository.countByUserId(tenant.getId());
        long pendingCount = enquiryRepository.countByTenantIdAndStatus(tenant.getId(), EnquiryStatus.PENDING);
        long acceptedCount = enquiryRepository.countByTenantIdAndStatus(tenant.getId(), EnquiryStatus.ACCEPTED);
        long declinedCount = enquiryRepository.countByTenantIdAndStatus(tenant.getId(), EnquiryStatus.DECLINED);

        List<PropertyResponse> recentlyViewed = recentlyViewedRepository
                .findTop5ByUserIdOrderByViewedAtDesc(tenant.getId())
                .stream()
                .map(rv -> PropertyResponse.from(rv.getProperty()))
                .toList();

        log.info("Tenant dashboard fetched: tenantId={}", tenant.getId());
        return new TenantDashboardResponse(savedCount, pendingCount, acceptedCount, declinedCount, recentlyViewed);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logPropertyView(Long propertyId, String userEmail) {
        if (userEmail == null) return;

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return;

        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) return;

        Optional<RecentlyViewed> existing = recentlyViewedRepository
                .findByUserIdAndPropertyId(user.getId(), propertyId);

        if (existing.isPresent()) {
            // Update the viewed_at timestamp (the @PreUpdate handles it on save)
            existing.get().setViewedAt(Instant.now());
            recentlyViewedRepository.save(existing.get());
        } else {
            // Insert new record
            recentlyViewedRepository.save(new RecentlyViewed(user, property));

            // Prune oldest if user has > 5 records
            List<RecentlyViewed> all = recentlyViewedRepository
                    .findAllByUserIdOrderByViewedAtDesc(user.getId());
            if (all.size() > 5) {
                List<RecentlyViewed> toDelete = all.subList(5, all.size());
                recentlyViewedRepository.deleteAll(toDelete);
            }
        }

        log.debug("Property view logged: userId={}, propertyId={}", user.getId(), propertyId);
    }
}
