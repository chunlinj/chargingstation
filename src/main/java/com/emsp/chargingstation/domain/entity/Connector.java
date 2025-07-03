package com.emsp.chargingstation.domain.entity;

import com.emsp.chargingstation.domain.enums.ConnectorStandard;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "connectors")
public class Connector extends BaseEntity {
    
    @NotNull(message = "Connector standard is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectorStandard standard;
    
    @NotNull(message = "Max power is required")
    @Positive(message = "Max power must be positive")
    @Column(name = "max_power_kw", nullable = false, precision = 8, scale = 2)
    private BigDecimal maxPowerKw;
    
    @NotNull(message = "Voltage is required")
    @Positive(message = "Voltage must be positive")
    @Column(nullable = false)
    private Integer voltage;
    
    @NotNull(message = "Amperage is required")
    @Positive(message = "Amperage must be positive")
    @Column(nullable = false)
    private Integer amperage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evse_id", nullable = false)
    private Evse evse;
    
    @Column(length = 500)
    private String description;
    
    protected Connector() {
    }
    
    public Connector(ConnectorStandard standard, BigDecimal maxPowerKw, Integer voltage, Integer amperage) {
        this.standard = standard;
        this.maxPowerKw = maxPowerKw;
        this.voltage = voltage;
        this.amperage = amperage;
    }
    
    // Business methods
    public boolean isDcFastCharging() {
        return maxPowerKw.compareTo(BigDecimal.valueOf(50)) >= 0;
    }
    
    public boolean isCompatibleWith(ConnectorStandard requiredStandard) {
        return this.standard == requiredStandard;
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
    
    public Evse getEvse() {
        return evse;
    }
    
    public void setEvse(Evse evse) {
        this.evse = evse;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 