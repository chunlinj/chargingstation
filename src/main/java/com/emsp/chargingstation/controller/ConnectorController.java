package com.emsp.chargingstation.controller;

import com.emsp.chargingstation.dto.request.CreateConnectorRequest;
import com.emsp.chargingstation.dto.response.ConnectorResponse;
import com.emsp.chargingstation.service.ConnectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Connector Management", description = "APIs for managing charging connectors")
public class ConnectorController {
    
    private final ConnectorService connectorService;
    
    public ConnectorController(ConnectorService connectorService) {
        this.connectorService = connectorService;
    }
    
    @PostMapping("/evses/{evseId}/connectors")
    @Operation(summary = "Add connector to an EVSE")
    public ResponseEntity<ConnectorResponse> createConnector(
        @Parameter(description = "EVSE ID") @PathVariable UUID evseId,
        @Valid @RequestBody CreateConnectorRequest request
    ) {
        ConnectorResponse response = connectorService.createConnector(evseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/connectors/{connectorId}")
    @Operation(summary = "Get connector by ID")
    public ResponseEntity<ConnectorResponse> getConnectorById(
        @Parameter(description = "Connector ID") @PathVariable UUID connectorId
    ) {
        ConnectorResponse response = connectorService.getConnectorById(connectorId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/evses/{evseId}/connectors")
    @Operation(summary = "Get all connectors for an EVSE")
    public ResponseEntity<List<ConnectorResponse>> getConnectorsByEvseId(
        @Parameter(description = "EVSE ID") @PathVariable UUID evseId
    ) {
        List<ConnectorResponse> response = connectorService.getConnectorsByEvseId(evseId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/connectors/{connectorId}")
    @Operation(summary = "Delete a connector")
    public ResponseEntity<Void> deleteConnector(
        @Parameter(description = "Connector ID") @PathVariable UUID connectorId
    ) {
        connectorService.deleteConnector(connectorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/evses/evse-id/{evseId}/connectors")
    @Operation(summary = "Add connector to an EVSE by EVSE identifier")
    public ResponseEntity<ConnectorResponse> createConnectorByEvseId(
        @Parameter(description = "EVSE identifier (e.g., CN*TST*EVSE001)") @PathVariable String evseId,
        @Valid @RequestBody CreateConnectorRequest request
    ) {
        ConnectorResponse response = connectorService.createConnectorByEvseId(evseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/evses/evse-id/{evseId}/connectors")
    @Operation(summary = "Get all connectors for an EVSE by EVSE identifier")
    public ResponseEntity<List<ConnectorResponse>> getConnectorsByEvseIdentifier(
        @Parameter(description = "EVSE identifier (e.g., CN*TST*EVSE001)") @PathVariable String evseId
    ) {
        List<ConnectorResponse> response = connectorService.getConnectorsByEvseIdentifier(evseId);
        return ResponseEntity.ok(response);
    }
} 