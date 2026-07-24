package com.rentnest.controller;

import com.rentnest.dto.request.LoginRequest;
import com.rentnest.dto.request.RegisterRequest;
import com.rentnest.dto.response.AuthResponse;
import com.rentnest.dto.response.UserResponse;
import com.rentnest.exception.DuplicateResourceException;
import com.rentnest.config.SecurityConfig;
import com.rentnest.security.JwtAuthenticationFilter;
import com.rentnest.security.JwtTokenProvider;
import com.rentnest.security.UserDetailsServiceImpl;
import com.rentnest.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void registerWithValidInputReturnsCreated() throws Exception {
        RegisterRequest request = new RegisterRequest("Priya Shah", "priya@example.com", "9876543210", "Strong!Pass1");
        UserResponse userResponse = new UserResponse(1L, "Priya Shah", "priya@example.com", "+919876543210", "ROLE_USER", Instant.now());
        AuthResponse authResponse = new AuthResponse("mock.jwt.token", "Bearer", Instant.now().plusSeconds(900), userResponse);

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.name").value("Priya Shah"))
                .andExpect(jsonPath("$.user.email").value("priya@example.com"))
                .andExpect(jsonPath("$.user.phone").value("+919876543210"))
                .andExpect(jsonPath("$.user.role").value("ROLE_USER"));
    }

    @Test
    void registerWithInvalidInputReturnsBadRequest() throws Exception {
        // Invalid email, weak password, invalid phone format
        RegisterRequest request = new RegisterRequest("", "invalid-email", "12345", "weak");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void registerWithDuplicateEmailReturnsConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("Priya Shah", "priya@example.com", "9876543210", "Strong!Pass1");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("An account with this email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.message").value("An account with this email already exists"));
    }

    @Test
    void loginWithValidInputReturnsOk() throws Exception {
        LoginRequest request = new LoginRequest("priya@example.com", "Strong!Pass1");
        UserResponse userResponse = new UserResponse(1L, "Priya Shah", "priya@example.com", "+919876543210", "ROLE_USER", Instant.now());
        AuthResponse authResponse = new AuthResponse("mock.jwt.token", "Bearer", Instant.now().plusSeconds(900), userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.id").value(1L));
    }

    @Test
    void loginWithInvalidInputReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email", "");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
