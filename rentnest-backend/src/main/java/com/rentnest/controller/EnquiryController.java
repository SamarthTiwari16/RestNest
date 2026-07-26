package com.rentnest.controller;

import com.rentnest.dto.request.EnquiryRequest;
import com.rentnest.dto.response.EnquiryResponse;
import com.rentnest.service.EnquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/enquiries")
@Tag(name = "Enquiries")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @PostMapping("/property/{propertyId}")
    @Operation(summary = "Submit a new enquiry on an active property listing")
    public ResponseEntity<EnquiryResponse> sendEnquiry(
            @PathVariable Long propertyId,
            @Valid @RequestBody EnquiryRequest request,
            Principal principal
    ) {
        EnquiryResponse response = enquiryService.sendEnquiry(propertyId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{enquiryId}/accept")
    @Operation(summary = "Accept enquiry and share owner contact details")
    public ResponseEntity<EnquiryResponse> acceptEnquiry(
            @PathVariable Long enquiryId,
            Principal principal
    ) {
        EnquiryResponse response = enquiryService.acceptEnquiry(enquiryId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{enquiryId}/decline")
    @Operation(summary = "Decline enquiry")
    public ResponseEntity<EnquiryResponse> declineEnquiry(
            @PathVariable Long enquiryId,
            Principal principal
    ) {
        EnquiryResponse response = enquiryService.declineEnquiry(enquiryId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    @Operation(summary = "Get all sent enquiries (Tenant portal)")
    public ResponseEntity<List<EnquiryResponse>> getSentEnquiries(Principal principal) {
        List<EnquiryResponse> response = enquiryService.getSentEnquiries(principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/received")
    @Operation(summary = "Get all received enquiries (Owner portal)")
    public ResponseEntity<List<EnquiryResponse>> getReceivedEnquiries(Principal principal) {
        List<EnquiryResponse> response = enquiryService.getReceivedEnquiries(principal.getName());
        return ResponseEntity.ok(response);
    }
}
