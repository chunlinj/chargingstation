package com.emsp.chargingstation.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalTime;

public class UpdateLocationRequest {
    
    @Size(max = 255, message = "Location name cannot exceed 255 characters")
    private String name;
    
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
    
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;
    
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;
    
    private LocalTime openingTime;
    
    private LocalTime closingTime;
    
    private Boolean is24Hours;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    // Constructors
    public UpdateLocationRequest() {
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public LocalTime getOpeningTime() {
        return openingTime;
    }
    
    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }
    
    public LocalTime getClosingTime() {
        return closingTime;
    }
    
    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
    
    public Boolean getIs24Hours() {
        return is24Hours;
    }
    
    public void setIs24Hours(Boolean is24Hours) {
        this.is24Hours = is24Hours;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 