package com.emsp.chargingstation.dto.request;

import com.emsp.chargingstation.domain.enums.EvseStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateEvseStatusRequest {
    
    @NotNull(message = "Status is required")
    private EvseStatus status;
    
    // Constructors
    public UpdateEvseStatusRequest() {
    }
    
    public UpdateEvseStatusRequest(EvseStatus status) {
        this.status = status;
    }
    
    // Getters and Setters
    public EvseStatus getStatus() {
        return status;
    }
    
    public void setStatus(EvseStatus status) {
        this.status = status;
    }
} 