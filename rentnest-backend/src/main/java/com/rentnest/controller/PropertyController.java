package com.rentnest.controller;

import com.rentnest.dto.request.PropertyRequest;
import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.service.PropertyService;
import com.rentnest.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/properties")
@Tag(name = "Properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final ImageStorageService imageStorageService;

    public PropertyController(PropertyService propertyService, ImageStorageService imageStorageService) {
        this.propertyService = propertyService;
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/images/upload")
    @Operation(summary = "Upload property listing image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = imageStorageService.storeImage(file);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter active property listings with pagination")
    public ResponseEntity<Page<PropertyResponse>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String locality,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) Integer bhk,
            @RequestParam(required = false) Boolean furnished,
            @RequestParam(required = false) Boolean parking,
            @RequestParam(required = false) Boolean petFriendly,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate availableFrom,
            @RequestParam(required = false) String propertyType,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PropertyResponse> response = propertyService.searchProperties(
                city, locality, minRent, maxRent, bhk, furnished, parking, petFriendly, availableFrom, propertyType, pageable
        );
        return ResponseEntity.ok(response);
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
