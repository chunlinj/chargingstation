package com.emsp.chargingstation.dto.request;

import com.emsp.chargingstation.domain.enums.ConnectorStandard;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateConnectorRequest {
    
    @NotNull(message = "Connector standard is required")
    private ConnectorStandard standard;
    
    @NotNull(message = "Max power is required")
    @Positive(message = "Max power must be positive")
    private BigDecimal maxPowerKw;
    
    @NotNull(message = "Voltage is required")
    @Positive(message = "Voltage must be positive")
    private Integer voltage;
    
    @NotNull(message = "Amperage is required")
    @Positive(message = "Amperage must be positive")
    private Integer amperage;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    // Constructors
    public CreateConnectorRequest() {
    }
    
    public CreateConnectorRequest(ConnectorStandard standard, BigDecimal maxPowerKw, Integer voltage, Integer amperage) {
        this.standard = standard;
        this.maxPowerKw = maxPowerKw;
        this.voltage = voltage;
        this.amperage = amperage;
    }
    
    // Getters and Setters
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
} 