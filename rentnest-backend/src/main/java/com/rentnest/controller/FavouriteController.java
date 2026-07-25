package com.rentnest.controller;

import com.rentnest.dto.response.PropertyResponse;
import com.rentnest.service.FavouriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/favourites")
@Tag(name = "Favourites")
public class FavouriteController {

    private final FavouriteService favouriteService;

    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    @PostMapping("/{propertyId}")
    @Operation(summary = "Save property to favourites")
    public ResponseEntity<Void> addFavourite(@PathVariable Long propertyId, Principal principal) {
        favouriteService.addFavourite(propertyId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{propertyId}")
    @Operation(summary = "Remove property from favourites")
    public ResponseEntity<Void> removeFavourite(@PathVariable Long propertyId, Principal principal) {
        favouriteService.removeFavourite(propertyId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get list of all saved properties")
    public ResponseEntity<List<PropertyResponse>> getMyFavourites(Principal principal) {
        List<PropertyResponse> response = favouriteService.getMyFavourites(principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ids")
    @Operation(summary = "Get list of all saved property IDs")
    public ResponseEntity<List<Long>> getMyFavouritePropertyIds(Principal principal) {
        List<Long> response = favouriteService.getMyFavouritePropertyIds(principal.getName());
        return ResponseEntity.ok(response);
    }
}
