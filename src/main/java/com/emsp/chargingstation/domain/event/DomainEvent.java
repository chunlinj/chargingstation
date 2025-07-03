package com.emsp.chargingstation.domain.event;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent {
    
    private final UUID eventId;
    private final Instant occurredOn;
    private final String eventType;
    
    protected DomainEvent(String eventType) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.eventType = eventType;
    }
    
    public UUID getEventId() {
        return eventId;
    }
    
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    public String getEventType() {
        return eventType;
    }
} 