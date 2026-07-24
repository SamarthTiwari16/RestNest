package com.rentnest.service.impl;

import com.rentnest.config.JwtProperties;
import com.rentnest.dto.request.LoginRequest;
import com.rentnest.dto.request.RegisterRequest;
import com.rentnest.dto.response.AuthResponse;
import com.rentnest.entity.Role;
import com.rentnest.entity.User;
import com.rentnest.exception.DuplicateResourceException;
import com.rentnest.repository.UserRepository;
import com.rentnest.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder, authenticationManager, jwtTokenProvider,
                new JwtProperties("a-secure-test-secret-that-is-at-least-32-chars", 900_000));
    }

    @Test
    void registerHashesPasswordAndReturnsToken() {
        RegisterRequest request = new RegisterRequest("Priya Shah", "PRIYA@example.com", "9876543210", "Strong!Pass1");
        User saved = new User("Priya Shah", "priya@example.com", "+919876543210", "$2a$hash", Role.ROLE_USER);
        when(userRepository.existsByEmail("priya@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Strong!Pass1")).thenReturn("$2a$hash");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(jwtTokenProvider.generateToken(saved)).thenReturn("signed.jwt");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> captured = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captured.capture());
        assertThat(captured.getValue().getPasswordHash()).isEqualTo("$2a$hash");
        assertThat(captured.getValue().getEmail()).isEqualTo("priya@example.com");
        assertThat(response.accessToken()).isEqualTo("signed.jwt");
        assertThat(response.user().phone()).isEqualTo("+919876543210");
    }

    @Test
    void registerRejectsDuplicateEmailBeforeHashing() {
        RegisterRequest request = new RegisterRequest("Priya Shah", "priya@example.com", "9876543210", "Strong!Pass1");
        when(userRepository.existsByEmail("priya@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request)).isInstanceOf(DuplicateResourceException.class);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginAuthenticatesThenIssuesToken() {
        User user = new User("Priya Shah", "priya@example.com", "+919876543210", "$2a$hash", Role.ROLE_USER);
        when(userRepository.findByEmail("priya@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(user)).thenReturn("signed.jwt");

        AuthResponse response = authService.login(new LoginRequest("PRIYA@example.com", "Strong!Pass1"));

        verify(authenticationManager).authenticate(any());
        assertThat(response.accessToken()).isEqualTo("signed.jwt");
    }
}
