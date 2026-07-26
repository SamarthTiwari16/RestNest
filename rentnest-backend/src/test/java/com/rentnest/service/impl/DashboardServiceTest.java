package com.rentnest.service.impl;

import com.rentnest.dto.response.OwnerDashboardResponse;
import com.rentnest.dto.response.TenantDashboardResponse;
import com.rentnest.entity.*;
import com.rentnest.entity.Role;
import com.rentnest.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private EnquiryRepository enquiryRepository;
    @Mock private FavouriteRepository favouriteRepository;
    @Mock private RecentlyViewedRepository recentlyViewedRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private User owner;
    private User tenant;
    private Property property;

    @BeforeEach
    void setUp() {
        owner = new User("Test Owner", "owner@example.com", "9999900000", "hash", Role.ROLE_USER);
        owner.setId(1L);

        tenant = new User("Test Tenant", "tenant@example.com", "8888800000", "hash", Role.ROLE_USER);
        tenant.setId(2L);

        property = new Property(owner, "Test Property", "City", "Locality",
                BigDecimal.valueOf(15000), 2, "APARTMENT", true, false, false,
                LocalDate.now().plusDays(7), PropertyStatus.ACTIVE);
        property.setId(10L);
    }

    // ========== OWNER DASHBOARD TESTS ==========

    @Test
    void getOwnerDashboard_returnsCorrectCounts() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.countByOwnerId(1L)).thenReturn(5L);
        when(propertyRepository.countByOwnerIdAndStatus(1L, PropertyStatus.ACTIVE)).thenReturn(3L);
        when(propertyRepository.countByOwnerIdAndStatus(1L, PropertyStatus.RENTED)).thenReturn(1L);
        when(enquiryRepository.countByOwnerId(1L)).thenReturn(8L);

        OwnerDashboardResponse resp = dashboardService.getOwnerDashboard("owner@example.com");

        assertThat(resp.totalProperties()).isEqualTo(5L);
        assertThat(resp.activeListings()).isEqualTo(3L);
        assertThat(resp.rentedCount()).isEqualTo(1L);
        assertThat(resp.totalEnquiriesReceived()).isEqualTo(8L);
    }

    @Test
    void getOwnerDashboard_zeroWhenNoProperties() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.countByOwnerId(1L)).thenReturn(0L);
        when(propertyRepository.countByOwnerIdAndStatus(any(), any())).thenReturn(0L);
        when(enquiryRepository.countByOwnerId(1L)).thenReturn(0L);

        OwnerDashboardResponse resp = dashboardService.getOwnerDashboard("owner@example.com");

        assertThat(resp.totalProperties()).isZero();
        assertThat(resp.activeListings()).isZero();
        assertThat(resp.rentedCount()).isZero();
        assertThat(resp.totalEnquiriesReceived()).isZero();
    }

    // ========== TENANT DASHBOARD TESTS ==========

    @Test
    void getTenantDashboard_returnsCorrectCounts() {
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(favouriteRepository.countByUserId(2L)).thenReturn(4L);
        when(enquiryRepository.countByTenantIdAndStatus(2L, EnquiryStatus.PENDING)).thenReturn(2L);
        when(enquiryRepository.countByTenantIdAndStatus(2L, EnquiryStatus.ACCEPTED)).thenReturn(1L);
        when(enquiryRepository.countByTenantIdAndStatus(2L, EnquiryStatus.DECLINED)).thenReturn(0L);
        when(recentlyViewedRepository.findTop5ByUserIdOrderByViewedAtDesc(2L)).thenReturn(List.of());

        TenantDashboardResponse resp = dashboardService.getTenantDashboard("tenant@example.com");

        assertThat(resp.savedPropertiesCount()).isEqualTo(4L);
        assertThat(resp.pendingEnquiriesCount()).isEqualTo(2L);
        assertThat(resp.acceptedEnquiriesCount()).isEqualTo(1L);
        assertThat(resp.declinedEnquiriesCount()).isZero();
        assertThat(resp.recentlyViewed()).isEmpty();
    }

    @Test
    void getTenantDashboard_recentlyViewedMapsToPropertyResponse() {
        RecentlyViewed rv = new RecentlyViewed(tenant, property);

        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(favouriteRepository.countByUserId(2L)).thenReturn(0L);
        when(enquiryRepository.countByTenantIdAndStatus(any(), any())).thenReturn(0L);
        when(recentlyViewedRepository.findTop5ByUserIdOrderByViewedAtDesc(2L)).thenReturn(List.of(rv));

        TenantDashboardResponse resp = dashboardService.getTenantDashboard("tenant@example.com");

        assertThat(resp.recentlyViewed()).hasSize(1);
        assertThat(resp.recentlyViewed().get(0).title()).isEqualTo("Test Property");
    }

    // ========== VIEW LOG TESTS ==========

    @Test
    void logPropertyView_createsNewRecordIfNoneExists() {
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(recentlyViewedRepository.findByUserIdAndPropertyId(2L, 10L)).thenReturn(Optional.empty());
        when(recentlyViewedRepository.findAllByUserIdOrderByViewedAtDesc(2L)).thenReturn(List.of());

        dashboardService.logPropertyView(10L, "tenant@example.com");

        verify(recentlyViewedRepository).save(any(RecentlyViewed.class));
    }

    @Test
    void logPropertyView_updatesExistingRecord() {
        RecentlyViewed rv = new RecentlyViewed(tenant, property);
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(recentlyViewedRepository.findByUserIdAndPropertyId(2L, 10L)).thenReturn(Optional.of(rv));

        dashboardService.logPropertyView(10L, "tenant@example.com");

        verify(recentlyViewedRepository).save(rv);
    }

    @Test
    void logPropertyView_prunesOldRecordsWhenExceeds5() {
        // Create 6 previously saved items
        List<RecentlyViewed> sixItems = List.of(
                new RecentlyViewed(tenant, property),
                new RecentlyViewed(tenant, property),
                new RecentlyViewed(tenant, property),
                new RecentlyViewed(tenant, property),
                new RecentlyViewed(tenant, property),
                new RecentlyViewed(tenant, property)
        );

        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(recentlyViewedRepository.findByUserIdAndPropertyId(2L, 10L)).thenReturn(Optional.empty());
        when(recentlyViewedRepository.findAllByUserIdOrderByViewedAtDesc(2L)).thenReturn(sixItems);

        dashboardService.logPropertyView(10L, "tenant@example.com");

        // Should delete the 6th (oldest) item
        verify(recentlyViewedRepository).deleteAll(argThat(list -> {
            var l = new java.util.ArrayList<>();
            list.forEach(l::add);
            return l.size() == 1;
        }));
    }

    @Test
    void logPropertyView_doesNothingWhenUserEmailIsNull() {
        dashboardService.logPropertyView(10L, null);
        verifyNoInteractions(recentlyViewedRepository);
    }
}
