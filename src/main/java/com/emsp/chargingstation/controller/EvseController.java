package com.emsp.chargingstation.controller;

import com.emsp.chargingstation.domain.enums.EvseStatus;
import com.emsp.chargingstation.dto.request.CreateEvseRequest;
import com.emsp.chargingstation.dto.request.UpdateEvseStatusRequest;
import com.emsp.chargingstation.dto.response.EvseResponse;
import com.emsp.chargingstation.service.EvseService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "EVSE Management", description = "APIs for managing Electric Vehicle Supply Equipment")
public class EvseController {
    
    private final EvseService evseService;
    
    public EvseController(EvseService evseService) {
        this.evseService = evseService;
    }
    
    @PostMapping("/locations/{locationId}/evses")
    @Operation(summary = "Add EVSE to a location")
    public ResponseEntity<EvseResponse> createEvse(
        @Parameter(description = "Location ID") @PathVariable UUID locationId,
        @Valid @RequestBody CreateEvseRequest request
    ) {
        EvseResponse response = evseService.createEvse(locationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/evses/{evseId}")
    @Operation(summary = "Get EVSE by ID")
    public ResponseEntity<EvseResponse> getEvseById(
        @Parameter(description = "EVSE ID") @PathVariable UUID evseId
    ) {
        EvseResponse response = evseService.getEvseById(evseId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/evses/evse-id/{evseId}")
    @Operation(summary = "Get EVSE by EVSE identifier")
    public ResponseEntity<EvseResponse> getEvseByEvseId(
        @Parameter(description = "EVSE identifier (e.g., US*ABC*EVSE123456)") @PathVariable String evseId
    ) {
        EvseResponse response = evseService.getEvseByEvseId(evseId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/locations/{locationId}/evses")
    @Operation(summary = "Get all EVSEs for a location")
    public ResponseEntity<List<EvseResponse>> getEvsesByLocationId(
        @Parameter(description = "Location ID") @PathVariable UUID locationId
    ) {
        List<EvseResponse> response = evseService.getEvsesByLocationId(locationId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/evses")
    @Operation(summary = "Get EVSEs by status")
    public ResponseEntity<Page<EvseResponse>> getEvsesByStatus(
        @Parameter(description = "EVSE status") @RequestParam EvseStatus status,
        Pageable pageable
    ) {
        Page<EvseResponse> response = evseService.getEvsesByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/evses/updated-after")
    @Operation(summary = "Get EVSEs updated after specific timestamp")
    public ResponseEntity<Page<EvseResponse>> getEvsesUpdatedAfter(
        @Parameter(description = "Last updated timestamp (ISO-8601 format)") @RequestParam Instant lastUpdated,
        Pageable pageable
    ) {
        Page<EvseResponse> response = evseService.getEvsesUpdatedAfter(lastUpdated, pageable);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/evses/{evseId}/status")
    @Operation(summary = "Update EVSE status")
    public ResponseEntity<EvseResponse> updateEvseStatus(
        @Parameter(description = "EVSE ID") @PathVariable UUID evseId,
        @Valid @RequestBody UpdateEvseStatusRequest request
    ) {
        EvseResponse response = evseService.updateEvseStatus(evseId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/evses/{evseId}")
    @Operation(summary = "Delete an EVSE")
    public ResponseEntity<Void> deleteEvse(
        @Parameter(description = "EVSE ID") @PathVariable UUID evseId
    ) {
        evseService.deleteEvse(evseId);
        return ResponseEntity.noContent().build();
    }
} 