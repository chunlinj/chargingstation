package com.emsp.chargingstation.dto.response;

import com.emsp.chargingstation.domain.enums.ConnectorStandard;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ConnectorResponse {
    
    private UUID id;
    private ConnectorStandard standard;
    private BigDecimal maxPowerKw;
    private Integer voltage;
    private Integer amperage;
    private String description;
    private Instant createdAt;
    private Instant lastUpdated;
    
    // Constructors
    public ConnectorResponse() {
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public ConnectorStandard getStandard() {
        return standard;
    }
    
    public void setStandard(ConnectorStandard standard) {
        this.standard = standard;
    }
    
    public BigDecimal getMaxPowerKw() {
        return maxPowerKw;
    }
    
    public void setMaxPowerKw(BigDecimal maxPowerKw) {
        this.maxPowerKw = maxPowerKw;
    }
    
    public Integer getVoltage() {
        return voltage;
    }
    
    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }
    
    public Integer getAmperage() {
        return amperage;
    }
    
    public void setAmperage(Integer amperage) {
        this.amperage = amperage;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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