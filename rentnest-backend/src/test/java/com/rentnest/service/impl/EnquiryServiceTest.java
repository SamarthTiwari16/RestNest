package com.rentnest.service.impl;

import com.rentnest.dto.request.EnquiryRequest;
import com.rentnest.dto.response.EnquiryResponse;
import com.rentnest.entity.*;
import com.rentnest.exception.ValidationException;
import com.rentnest.repository.EnquiryRepository;
import com.rentnest.repository.PropertyRepository;
import com.rentnest.repository.UserRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnquiryServiceTest {

    @Mock
    private EnquiryRepository enquiryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private EnquiryServiceImpl enquiryService;

    private User owner;
    private User tenant;
    private Property property;
    private EnquiryRequest request;

    @BeforeEach
    void setUp() {
        owner = new User("Owner User", "owner@example.com", "9876543210", "$2a$hash", Role.ROLE_USER);
        setId(owner, 1L);

        tenant = new User("Tenant User", "tenant@example.com", "9876543211", "$2a$hash", Role.ROLE_USER);
        setId(tenant, 2L);

        property = new Property(owner, "Cozy Villa", "Bangalore", "Whitefield", BigDecimal.valueOf(30000), 3, "VILLA", true, true, true, LocalDate.now(), PropertyStatus.ACTIVE);
        setId(property, 10L);

        request = new EnquiryRequest("Hi, I want to rent this cozy villa.", LocalDate.now().plusDays(5), 2);
    }

    @Test
    void sendEnquirySavesEnquirySuccessfully() {
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(enquiryRepository.existsByPropertyIdAndTenantIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(false);
        
        Enquiry savedEnquiry = new Enquiry(property, tenant, owner, request.message(), request.moveInDate(), request.occupants());
        setId(savedEnquiry, 100L);
        when(enquiryRepository.save(any(Enquiry.class))).thenReturn(savedEnquiry);

        EnquiryResponse result = enquiryService.sendEnquiry(10L, request, "tenant@example.com");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.status()).isEqualTo(EnquiryStatus.PENDING);
        // Initially masked
        assertThat(result.owner().email()).isNull();
        assertThat(result.owner().phone()).isNull();
        verify(enquiryRepository).save(any(Enquiry.class));
    }

    @Test
    void sendEnquiryThrowsIfPropertyNotActive() {
        property.setStatus(PropertyStatus.RENTED);
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));

        assertThatThrownBy(() -> enquiryService.sendEnquiry(10L, request, "tenant@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot enquire on a property that is not ACTIVE");
    }

    @Test
    void sendEnquiryThrowsIfTenantIsOwner() {
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));

        assertThatThrownBy(() -> enquiryService.sendEnquiry(10L, request, "owner@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("You cannot submit an enquiry for your own property");
    }

    @Test
    void sendEnquiryThrowsIfDuplicateActiveEnquiryExists() {
        when(userRepository.findByEmail("tenant@example.com")).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(enquiryRepository.existsByPropertyIdAndTenantIdAndStatusIn(anyLong(), anyLong(), anyList())).thenReturn(true);

        assertThatThrownBy(() -> enquiryService.sendEnquiry(10L, request, "tenant@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("You already have an active enquiry for this property");
    }

    @Test
    void acceptEnquirySetsStatusToAcceptedAndUnmasksContact() {
        Enquiry enquiry = new Enquiry(property, tenant, owner, request.message(), request.moveInDate(), request.occupants());
        enquiry.setStatus(EnquiryStatus.PENDING);
        setId(enquiry, 100L);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(enquiryRepository.findById(100L)).thenReturn(Optional.of(enquiry));
        when(enquiryRepository.save(any(Enquiry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EnquiryResponse result = enquiryService.acceptEnquiry(100L, "owner@example.com");

        assertThat(result.status()).isEqualTo(EnquiryStatus.ACCEPTED);
        // Owner contact revealed
        assertThat(result.owner().email()).isEqualTo("owner@example.com");
        assertThat(result.owner().phone()).isEqualTo("9876543210");
    }

    @Test
    void declineEnquirySetsStatusToDeclinedAndMasksContact() {
        Enquiry enquiry = new Enquiry(property, tenant, owner, request.message(), request.moveInDate(), request.occupants());
        enquiry.setStatus(EnquiryStatus.PENDING);
        setId(enquiry, 100L);

        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(enquiryRepository.findById(100L)).thenReturn(Optional.of(enquiry));
        when(enquiryRepository.save(any(Enquiry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EnquiryResponse result = enquiryService.declineEnquiry(100L, "owner@example.com");

        assertThat(result.status()).isEqualTo(EnquiryStatus.DECLINED);
        // Masked contact details
        assertThat(result.owner().email()).isNull();
        assertThat(result.owner().phone()).isNull();
    }

    private void setId(Object obj, Long id) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
