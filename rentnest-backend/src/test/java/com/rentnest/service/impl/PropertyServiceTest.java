package com.rentnest.service.impl;

import com.rentnest.dto.request.PropertyRequest;
import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyStatus;
import com.rentnest.entity.Role;
import com.rentnest.entity.User;
import com.rentnest.exception.ResourceNotFoundException;
import com.rentnest.exception.UnauthorizedActionException;
import com.rentnest.exception.ValidationException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private User owner;
    private User otherUser;
    private PropertyRequest request;

    @BeforeEach
    void setUp() {
        owner = new User("Ramesh Kumar", "ramesh@example.com", "9876543210", "$2a$hash", Role.ROLE_USER);
        // Using reflection or setting ID since it's database generated
        // We'll simulate ID using helper/mocking or field access. 
        // Note: User doesn't have setId, but since we are mocking, we can compare emails/names.
        // Wait, User has getId() but no setId. So we can use reflection to set ID or rely on mock checks.
        // Let's use reflection to set IDs for user and property to simulate DB state.
        setId(owner, 1L);

        otherUser = new User("Priya Shah", "priya@example.com", "9876543211", "$2a$hash", Role.ROLE_USER);
        setId(otherUser, 2L);

        request = new PropertyRequest(
                "Beautiful 2BHK Flat",
                "Bangalore",
                "Indiranagar",
                BigDecimal.valueOf(25000),
                2,
                "APARTMENT",
                true,
                true,
                true,
                LocalDate.now().plusDays(10)
        );
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

    @Test
    void createPropertySavesDraftProperty() {
        when(userRepository.findByEmail("ramesh@example.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
            Property p = invocation.getArgument(0);
            setId(p, 10L);
            return p;
        });

        PropertyResponse response = propertyService.createProperty(request, "ramesh@example.com");

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo(PropertyStatus.DRAFT);
        assertThat(response.title()).isEqualTo("Beautiful 2BHK Flat");
        verify(propertyRepository).save(any(Property.class));
    }

    @Test
    void updatePropertySucceedsForOwnerInDraft() {
        Property property = new Property(owner, "Old Title", "City", "Loc", BigDecimal.TEN, 1, "HOUSE", false, false, false, LocalDate.now(), PropertyStatus.DRAFT);
        setId(property, 10L);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.findByEmail("ramesh@example.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.save(any(Property.class))).thenAnswer(inv -> inv.getArgument(0));

        PropertyResponse response = propertyService.updateProperty(10L, request, "ramesh@example.com");

        assertThat(response.title()).isEqualTo("Beautiful 2BHK Flat");
        verify(propertyRepository).save(property);
    }

    @Test
    void updatePropertyThrowsValidationForNonDraft() {
        Property property = new Property(owner, "Title", "City", "Loc", BigDecimal.TEN, 1, "HOUSE", false, false, false, LocalDate.now(), PropertyStatus.PENDING_VERIFICATION);
        setId(property, 10L);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.findByEmail("ramesh@example.com")).thenReturn(Optional.of(owner));

        assertThatThrownBy(() -> propertyService.updateProperty(10L, request, "ramesh@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("DRAFT status");
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void updatePropertyThrowsUnauthorizedForNonOwner() {
        Property property = new Property(owner, "Title", "City", "Loc", BigDecimal.TEN, 1, "HOUSE", false, false, false, LocalDate.now(), PropertyStatus.DRAFT);
        setId(property, 10L);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.findByEmail("priya@example.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> propertyService.updateProperty(10L, request, "priya@example.com"))
                .isInstanceOf(UnauthorizedActionException.class)
                .hasMessageContaining("You do not own this property");
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void getPropertyByIdRestrictsDraftToOwnerAndAdmin() {
        Property property = new Property(owner, "Title", "City", "Loc", BigDecimal.TEN, 1, "HOUSE", false, false, false, LocalDate.now(), PropertyStatus.DRAFT);
        setId(property, 10L);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.findByEmail("priya@example.com")).thenReturn(Optional.of(otherUser));

        // Other user cannot read draft
        assertThatThrownBy(() -> propertyService.getPropertyById(10L, "priya@example.com"))
                .isInstanceOf(UnauthorizedActionException.class);

        // Owner can read draft
        when(userRepository.findByEmail("ramesh@example.com")).thenReturn(Optional.of(owner));
        PropertyResponse response = propertyService.getPropertyById(10L, "ramesh@example.com");
        assertThat(response.id()).isEqualTo(10L);
    }

    @Test
    void submitForVerificationTransitionsDraftToPending() {
        Property property = new Property(owner, "Title", "City", "Loc", BigDecimal.TEN, 1, "HOUSE", false, false, false, LocalDate.now(), PropertyStatus.DRAFT);
        setId(property, 10L);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(userRepository.findByEmail("ramesh@example.com")).thenReturn(Optional.of(owner));
        when(propertyRepository.save(any(Property.class))).thenAnswer(inv -> inv.getArgument(0));

        PropertyResponse response = propertyService.submitForVerification(10L, "ramesh@example.com");

        assertThat(response.status()).isEqualTo(PropertyStatus.PENDING_VERIFICATION);
        verify(propertyRepository).save(property);
    }
}
