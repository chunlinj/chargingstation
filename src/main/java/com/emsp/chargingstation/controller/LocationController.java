package com.emsp.chargingstation.controller;

import com.emsp.chargingstation.dto.request.CreateLocationRequest;
import com.emsp.chargingstation.dto.request.UpdateLocationRequest;
import com.emsp.chargingstation.dto.response.LocationResponse;
import com.emsp.chargingstation.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locations")
@Tag(name = "Location Management", description = "APIs for managing charging station locations")
public class LocationController {
    
    private final LocationService locationService;
    
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new charging location")
    public ResponseEntity<LocationResponse> createLocation(
        @Valid @RequestBody CreateLocationRequest request
    ) {
        LocationResponse response = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{locationId}")
    @Operation(summary = "Get location by ID")
    public ResponseEntity<LocationResponse> getLocationById(
        @Parameter(description = "Location ID") @PathVariable UUID locationId
    ) {
        LocationResponse response = locationService.getLocationById(locationId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all locations with pagination")
    public ResponseEntity<Page<LocationResponse>> getAllLocations(Pageable pageable) {
        Page<LocationResponse> response = locationService.getAllLocations(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search locations by name")
    public ResponseEntity<Page<LocationResponse>> searchLocationsByName(
        @Parameter(description = "Search term for location name") @RequestParam String name,
        Pageable pageable
    ) {
        Page<LocationResponse> response = locationService.searchLocationsByName(name, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/updated-after")
    @Operation(summary = "Get locations updated after specific timestamp")
    public ResponseEntity<Page<LocationResponse>> getLocationsUpdatedAfter(
        @Parameter(description = "Last updated timestamp (ISO-8601 format)") @RequestParam Instant lastUpdated,
        Pageable pageable
    ) {
        Page<LocationResponse> response = locationService.getLocationsUpdatedAfter(lastUpdated, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/bounds")
    @Operation(summary = "Get locations within geographic bounds")
    public ResponseEntity<Page<LocationResponse>> getLocationsInBounds(
        @Parameter(description = "Minimum latitude") @RequestParam Double minLatitude,
        @Parameter(description = "Maximum latitude") @RequestParam Double maxLatitude,
        @Parameter(description = "Minimum longitude") @RequestParam Double minLongitude,
        @Parameter(description = "Maximum longitude") @RequestParam Double maxLongitude,
        Pageable pageable
    ) {
        Page<LocationResponse> response = locationService.getLocationsInBounds(
            minLatitude, maxLatitude, minLongitude, maxLongitude, pageable
        );
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{locationId}")
    @Operation(summary = "Update location information")
    public ResponseEntity<LocationResponse> updateLocation(
        @Parameter(description = "Location ID") @PathVariable UUID locationId,
        @Valid @RequestBody UpdateLocationRequest request
    ) {
        LocationResponse response = locationService.updateLocation(locationId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{locationId}")
    @Operation(summary = "Delete a location")
    public ResponseEntity<Void> deleteLocation(
        @Parameter(description = "Location ID") @PathVariable UUID locationId
    ) {
        locationService.deleteLocation(locationId);
        return ResponseEntity.noContent().build();
    }
} 