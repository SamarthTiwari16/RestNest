package com.rentnest.security;

import com.rentnest.config.JwtProperties;
import com.rentnest.entity.Role;
import com.rentnest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties("a-secure-test-secret-that-is-at-least-32-chars-long", 3600000L); // 1 hour expiration
        jwtTokenProvider = new JwtTokenProvider(jwtProperties);
    }

    @Test
    void generateTokenProducesValidSignedToken() {
        User user = new User("Priya Shah", "priya@example.com", "+919876543210", "$2a$hash", Role.ROLE_USER);
        
        String token = jwtTokenProvider.generateToken(user);
        
        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.isValid(token)).isTrue();
    }

    @Test
    void extractEmailRetrievesCorrectSubject() {
        User user = new User("Priya Shah", "priya@example.com", "+919876543210", "$2a$hash", Role.ROLE_USER);
        String token = jwtTokenProvider.generateToken(user);
        
        String email = jwtTokenProvider.extractEmail(token);
        
        assertThat(email).isEqualTo("priya@example.com");
    }

    @Test
    void isValidReturnsFalseForMalformedOrExpiredTokens() {
        assertThat(jwtTokenProvider.isValid("invalid.token.here")).isFalse();
        assertThat(jwtTokenProvider.isValid(null)).isFalse();
        assertThat(jwtTokenProvider.isValid("")).isFalse();
    }

    @Test
    void isValidReturnsFalseForExpiredToken() {
        // Create token provider with 0 expiration time to trigger instant expiration
        JwtProperties expiredProperties = new JwtProperties("a-secure-test-secret-that-is-at-least-32-chars-long", 0L);
        JwtTokenProvider expiredProvider = new JwtTokenProvider(expiredProperties);
        User user = new User("Priya Shah", "priya@example.com", "+919876543210", "$2a$hash", Role.ROLE_USER);
        
        String token = expiredProvider.generateToken(user);
        
        assertThat(expiredProvider.isValid(token)).isFalse();
    }
}
