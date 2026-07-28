package com.rentnest.controller;

import com.rentnest.dto.request.RejectionRequest;
import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/properties")
@Tag(name = "Admin Moderation")
public class AdminController {

    private final PropertyService propertyService;

    public AdminController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/pending")
    @Operation(summary = "List all properties pending verification (Admin only)")
    public ResponseEntity<Page<PropertyResponse>> getPendingProperties(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(propertyService.getPendingProperties(pageable));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a pending property listing (Admin only)")
    public ResponseEntity<PropertyResponse> approveProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.approveProperty(id));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a pending property listing with a reason (Admin only)")
    public ResponseEntity<PropertyResponse> rejectProperty(
            @PathVariable Long id,
            @Valid @RequestBody RejectionRequest request
    ) {
        return ResponseEntity.ok(propertyService.rejectProperty(id, request.reason()));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate/archive an active listing (Admin only)")
    public ResponseEntity<PropertyResponse> deactivateProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.deactivateProperty(id));
    }
}
