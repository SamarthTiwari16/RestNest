package com.rentnest.controller;

import com.rentnest.dto.request.PropertyRequest;
import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping
    @Operation(summary = "Create a new property listing in DRAFT status")
    public ResponseEntity<PropertyResponse> createProperty(@Valid @RequestBody PropertyRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(propertyService.createProperty(request, principal.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing draft property listing")
    public PropertyResponse updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyRequest request, Principal principal) {
        return propertyService.updateProperty(id, request, principal.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property listing details by ID")
    public PropertyResponse getPropertyById(@PathVariable Long id, Principal principal) {
        return propertyService.getPropertyById(id, principal != null ? principal.getName() : null);
    }

    @GetMapping("/my")
    @Operation(summary = "List all property listings owned by the current user")
    public List<PropertyResponse> getMyProperties(Principal principal) {
        return propertyService.getMyProperties(principal.getName());
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit a draft property listing for admin verification")
    public PropertyResponse submitForVerification(@PathVariable Long id, Principal principal) {
        return propertyService.submitForVerification(id, principal.getName());
    }

    @PostMapping("/{id}/rent")
    @Operation(summary = "Mark an approved/active property listing as rented")
    public PropertyResponse markAsRented(@PathVariable Long id, Principal principal) {
        return propertyService.markAsRented(id, principal.getName());
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "Withdraw/archive a property listing")
    public PropertyResponse withdrawProperty(@PathVariable Long id, Principal principal) {
        return propertyService.withdrawProperty(id, principal.getName());
    }
}
