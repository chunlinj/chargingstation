package com.emsp.chargingstation.exception;

public class DuplicateResourceException extends ChargingStationException {
    
    public DuplicateResourceException(String resourceType, String identifier) {
        super(
            String.format("%s with identifier %s already exists", resourceType, identifier),
            "DUPLICATE_RESOURCE"
        );
    }
} 