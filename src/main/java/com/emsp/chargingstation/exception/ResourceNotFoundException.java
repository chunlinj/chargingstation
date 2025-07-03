package com.emsp.chargingstation.exception;

public class ResourceNotFoundException extends ChargingStationException {
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
            String.format("%s with identifier %s not found", resourceType, identifier),
            "RESOURCE_NOT_FOUND"
        );
    }
} 