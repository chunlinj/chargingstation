package com.emsp.chargingstation.dto.response;

import com.emsp.chargingstation.domain.enums.EvseStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EvseResponse {
    
    private UUID id;
    private String evseId;
    private EvseStatus status;
    private String description;
    private Set<ConnectorResponse> connectors;
    private Instant createdAt;
    private Instant lastUpdated;
    
    // Constructors
    public EvseResponse() {
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getEvseId() {
        return evseId;
    }
    
    public void setEvseId(String evseId) {
        this.evseId = evseId;
    }
    
    public EvseStatus getStatus() {
        return status;
    }
    
    public void setStatus(EvseStatus status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Set<ConnectorResponse> getConnectors() {
        return connectors;
    }
    
    public void setConnectors(Set<ConnectorResponse> connectors) {
        this.connectors = connectors;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
} 