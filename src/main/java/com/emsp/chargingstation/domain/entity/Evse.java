package com.emsp.chargingstation.domain.entity;

import com.emsp.chargingstation.domain.enums.EvseStatus;
import com.emsp.chargingstation.domain.valueobject.EvseId;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "evses")
public class Evse extends BaseEntity {
    
    @Embedded
    @Valid
    @NotNull(message = "EVSE ID is required")
    @AttributeOverride(name = "value", column = @Column(name = "evse_id", unique = true, nullable = false))
    private EvseId evseId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvseStatus status = EvseStatus.INITIAL;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @OneToMany(mappedBy = "evse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Connector> connectors = new HashSet<>();
    
    @Column(length = 500)
    private String description;
    
    protected Evse() {
    }
    
    public Evse(EvseId evseId) {
        this.evseId = evseId;
    }
    
    // Business methods
    public void changeStatus(EvseStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", this.status, newStatus)
            );
        }
        this.status = newStatus;
    }
    
    public void addConnector(Connector connector) {
        connectors.add(connector);
        connector.setEvse(this);
    }
    
    public void removeConnector(Connector connector) {
        connectors.remove(connector);
        connector.setEvse(null);
    }
    
    public boolean isOperational() {
        return status.isOperational();
    }
    
    public boolean isTerminal() {
        return status.isTerminal();
    }
    
    // Getters and Setters
    public EvseId getEvseId() {
        return evseId;
    }
    
    public void setEvseId(EvseId evseId) {
        this.evseId = evseId;
    }
    
    public EvseStatus getStatus() {
        return status;
    }
    
    public void setStatus(EvseStatus status) {
        this.status = status;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public Set<Connector> getConnectors() {
        return connectors;
    }
    
    public void setConnectors(Set<Connector> connectors) {
        this.connectors = connectors;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 