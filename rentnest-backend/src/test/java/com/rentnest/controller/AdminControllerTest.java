package com.rentnest.controller;

import com.rentnest.dto.request.RejectionRequest;
import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.dto.response.UserResponse;
import com.rentnest.entity.PropertyStatus;
import com.rentnest.config.SecurityConfig;
import com.rentnest.security.JwtAuthenticationFilter;
import com.rentnest.security.JwtTokenProvider;
import com.rentnest.security.UserDetailsServiceImpl;
import com.rentnest.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class, excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyService propertyService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private PropertyResponse createMockPropertyResponse(Long id, PropertyStatus status, String rejectionReason) {
        UserResponse owner = new UserResponse(1L, "Owner", "owner@example.com", "+919876543210", "ROLE_USER", Instant.now());
        return new PropertyResponse(
                id,
                owner,
                "Test Title",
                "Indore",
                "locality",
                BigDecimal.valueOf(15000),
                2,
                "APARTMENT",
                true,
                false,
                true,
                LocalDate.now().plusDays(5),
                status,
                Instant.now(),
                List.of(),
                rejectionReason
        );
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getPendingProperties_returnsPendingQueue() throws Exception {
        PropertyResponse p = createMockPropertyResponse(10L, PropertyStatus.PENDING_VERIFICATION, null);
        Page<PropertyResponse> page = new PageImpl<>(List.of(p), PageRequest.of(0, 10), 1);

        when(propertyService.getPendingProperties(any())).thenReturn(page);

        mockMvc.perform(get("/api/admin/properties/pending")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10L))
                .andExpect(jsonPath("$.content[0].status").value("PENDING_VERIFICATION"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getPendingProperties_restrictedForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/properties/pending")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void approveProperty_returnsApprovedProperty() throws Exception {
        PropertyResponse p = createMockPropertyResponse(10L, PropertyStatus.ACTIVE, null);

        when(propertyService.approveProperty(10L)).thenReturn(p);

        mockMvc.perform(post("/api/admin/properties/10/approve")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void rejectProperty_returnsRejectedProperty() throws Exception {
        PropertyResponse p = createMockPropertyResponse(10L, PropertyStatus.DRAFT, "Missing pictures");

        when(propertyService.rejectProperty(eq(10L), eq("Missing pictures"))).thenReturn(p);

        RejectionRequest req = new RejectionRequest("Missing pictures");

        mockMvc.perform(post("/api/admin/properties/10/reject")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.rejectionReason").value("Missing pictures"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deactivateProperty_returnsArchivedProperty() throws Exception {
        PropertyResponse p = createMockPropertyResponse(10L, PropertyStatus.ARCHIVED, null);

        when(propertyService.deactivateProperty(10L)).thenReturn(p);

        mockMvc.perform(post("/api/admin/properties/10/deactivate")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }
}
