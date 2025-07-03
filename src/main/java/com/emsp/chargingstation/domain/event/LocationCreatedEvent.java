package com.emsp.chargingstation.domain.event;

import java.util.UUID;

public class LocationCreatedEvent extends DomainEvent {
    
    private final UUID locationId;
    private final String locationName;
    private final String address;
    
    public LocationCreatedEvent(UUID locationId, String locationName, String address) {
        super("LOCATION_CREATED");
        this.locationId = locationId;
        this.locationName = locationName;
        this.address = address;
    }
    
    public UUID getLocationId() {
        return locationId;
    }
    
    public String getLocationName() {
        return locationName;
    }
    
    public String getAddress() {
        return address;
    }
} 