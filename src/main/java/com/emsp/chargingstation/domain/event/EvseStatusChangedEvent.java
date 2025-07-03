package com.emsp.chargingstation.domain.event;

import com.emsp.chargingstation.domain.enums.EvseStatus;
import java.util.UUID;

public class EvseStatusChangedEvent extends DomainEvent {
    
    private final UUID evseId;
    private final String evseIdentifier;
    private final EvseStatus oldStatus;
    private final EvseStatus newStatus;
    
    public EvseStatusChangedEvent(UUID evseId, String evseIdentifier, EvseStatus oldStatus, EvseStatus newStatus) {
        super("EVSE_STATUS_CHANGED");
        this.evseId = evseId;
        this.evseIdentifier = evseIdentifier;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
    
    public UUID getEvseId() {
        return evseId;
    }
    
    public String getEvseIdentifier() {
        return evseIdentifier;
    }
    
    public EvseStatus getOldStatus() {
        return oldStatus;
    }
    
    public EvseStatus getNewStatus() {
        return newStatus;
    }
} 