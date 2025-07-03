package com.emsp.chargingstation.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "locations")
public class Location extends BaseEntity {
    
    @NotBlank(message = "Location name cannot be blank")
    @Column(nullable = false, length = 255)
    private String name;
    
    @NotBlank(message = "Address cannot be blank")
    @Column(nullable = false, length = 500)
    private String address;
    
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(name = "opening_time")
    private LocalTime openingTime;
    
    @Column(name = "closing_time")
    private LocalTime closingTime;
    
    @Column(name = "is_24_hours")
    private Boolean is24Hours = false;
    
    @Column(length = 1000)
    private String description;
    
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Evse> evses = new HashSet<>();
    
    protected Location() {
    }
    
    public Location(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Business methods
    public void addEvse(Evse evse) {
        evses.add(evse);
        evse.setLocation(this);
    }
    
    public void removeEvse(Evse evse) {
        evses.remove(evse);
        evse.setLocation(null);
    }
    
    public boolean isOpen24Hours() {
        return Boolean.TRUE.equals(is24Hours);
    }
    
    public void setBusinessHours(LocalTime openingTime, LocalTime closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.is24Hours = false;
    }
    
    public void set24Hours() {
        this.is24Hours = true;
        this.openingTime = null;
        this.closingTime = null;
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
    
    public Set<Evse> getEvses() {
        return evses;
    }
    
    public void setEvses(Set<Evse> evses) {
        this.evses = evses;
    }
} 