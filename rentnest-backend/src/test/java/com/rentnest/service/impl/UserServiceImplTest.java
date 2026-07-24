package com.rentnest.service.impl;

import com.rentnest.dto.response.UserResponse;
import com.rentnest.entity.Role;
import com.rentnest.entity.User;
import com.rentnest.exception.ResourceNotFoundException;
import com.rentnest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getCurrentUserReturnsUserResponseWhenUserExists() {
        User user = new User("Priya Shah", "priya@example.com", "+919876543210", "$2a$hash", Role.ROLE_USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "createdAt", Instant.parse("2026-07-22T10:00:00Z"));

        when(userRepository.findByEmail("priya@example.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getCurrentUser("priya@example.com");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Priya Shah");
        assertThat(response.email()).isEqualTo("priya@example.com");
        assertThat(response.phone()).isEqualTo("+919876543210");
        assertThat(response.role()).isEqualTo("ROLE_USER");
        assertThat(response.createdAt()).isEqualTo(Instant.parse("2026-07-22T10:00:00Z"));
    }

    @Test
    void getCurrentUserThrowsResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser("nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Authenticated user no longer exists");
    }
}
