package com.emsp.chargingstation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateEvseRequest {
    
    @NotBlank(message = "EVSE ID cannot be blank")
    @Pattern(
        regexp = "^[A-Z]{2}\\*[A-Z0-9]{3}\\*[A-Z0-9]{1,30}$",
        message = "EVSE ID must follow the format: <CountryCode>*<PartyID>*<LocalEVSEID>"
    )
    private String evseId;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    // Constructors
    public CreateEvseRequest() {
    }
    
    public CreateEvseRequest(String evseId) {
        this.evseId = evseId;
    }
    
    // Getters and Setters
    public String getEvseId() {
        return evseId;
    }
    
    public void setEvseId(String evseId) {
        this.evseId = evseId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 