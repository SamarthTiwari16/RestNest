package com.rentnest.service.impl;

import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.entity.Favourite;
import com.rentnest.entity.Property;
import com.rentnest.entity.PropertyStatus;
import com.rentnest.entity.Role;
import com.rentnest.entity.User;
import com.rentnest.repository.FavouriteRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    private User user;
    private Property property;

    @BeforeEach
    void setUp() {
        user = new User("Test User", "test@example.com", "9876543210", "$2a$hash", Role.ROLE_USER);
        setId(user, 1L);

        property = new Property(user, "Title", "City", "Loc", BigDecimal.TEN, 2, "APARTMENT", false, false, false, LocalDate.now(), PropertyStatus.ACTIVE);
        setId(property, 10L);
    }

    @Test
    void addFavouriteSavesNewFavourite() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(favouriteRepository.existsByUserIdAndPropertyId(1L, 10L)).thenReturn(false);

        favouriteService.addFavourite(10L, "test@example.com");

        verify(favouriteRepository).save(any(Favourite.class));
    }

    @Test
    void addFavouriteReturnsSilentlyIfAlreadyExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(favouriteRepository.existsByUserIdAndPropertyId(1L, 10L)).thenReturn(true);

        favouriteService.addFavourite(10L, "test@example.com");

        verify(favouriteRepository, never()).save(any(Favourite.class));
    }

    @Test
    void removeFavouriteDeletesIfFound() {
        Favourite favourite = new Favourite(user, property);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(favouriteRepository.findByUserIdAndPropertyId(1L, 10L)).thenReturn(Optional.of(favourite));

        favouriteService.removeFavourite(10L, "test@example.com");

        verify(favouriteRepository).delete(favourite);
    }

    @Test
    void getMyFavouritesListsSavedProperties() {
        Favourite favourite = new Favourite(user, property);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(favouriteRepository.findAllByUserId(1L)).thenReturn(List.of(favourite));

        List<PropertyResponse> result = favouriteService.getMyFavourites("test@example.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Title");
    }

    @Test
    void getMyFavouritePropertyIdsReturnsIdsList() {
        Favourite favourite = new Favourite(user, property);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(favouriteRepository.findAllByUserId(1L)).thenReturn(List.of(favourite));

        List<Long> result = favouriteService.getMyFavouritePropertyIds("test@example.com");

        assertThat(result).containsExactly(10L);
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
