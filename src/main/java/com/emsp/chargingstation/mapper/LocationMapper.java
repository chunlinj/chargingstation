package com.emsp.chargingstation.mapper;

import com.emsp.chargingstation.domain.entity.Location;
import com.emsp.chargingstation.dto.request.CreateLocationRequest;
import com.emsp.chargingstation.dto.request.UpdateLocationRequest;
import com.emsp.chargingstation.dto.response.LocationResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationMapper {
    
    private final EvseMapper evseMapper;
    
    public LocationMapper(EvseMapper evseMapper) {
        this.evseMapper = evseMapper;
    }
    
    public Location toEntity(CreateLocationRequest request) {
        Location location = new Location(
            request.getName(),
            request.getAddress(),
            request.getLatitude(),
            request.getLongitude()
        );
        
        if (request.getIs24Hours() != null && request.getIs24Hours()) {
            location.set24Hours();
        } else if (request.getOpeningTime() != null && request.getClosingTime() != null) {
            location.setBusinessHours(request.getOpeningTime(), request.getClosingTime());
        }
        
        location.setDescription(request.getDescription());
        
        return location;
    }
    
    public LocationResponse toResponse(Location location) {
        LocationResponse response = new LocationResponse();
        response.setId(location.getId());
        response.setName(location.getName());
        response.setAddress(location.getAddress());
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        response.setOpeningTime(location.getOpeningTime());
        response.setClosingTime(location.getClosingTime());
        response.setIs24Hours(location.getIs24Hours());
        response.setDescription(location.getDescription());
        response.setCreatedAt(location.getCreatedAt());
        response.setLastUpdated(location.getLastUpdated());
        
        if (location.getEvses() != null && !location.getEvses().isEmpty()) {
            response.setEvses(
                location.getEvses().stream()
                    .map(evseMapper::toResponse)
                    .collect(Collectors.toSet())
            );
        }
        
        return response;
    }
    
    public List<LocationResponse> toResponseList(List<Location> locations) {
        return locations.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public void updateEntity(Location location, UpdateLocationRequest request) {
        if (request.getName() != null) {
            location.setName(request.getName());
        }
        if (request.getAddress() != null) {
            location.setAddress(request.getAddress());
        }
        if (request.getLatitude() != null) {
            location.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            location.setLongitude(request.getLongitude());
        }
        if (request.getDescription() != null) {
            location.setDescription(request.getDescription());
        }
        
        // Update business hours
        if (request.getIs24Hours() != null && request.getIs24Hours()) {
            location.set24Hours();
        } else if (request.getOpeningTime() != null && request.getClosingTime() != null) {
            location.setBusinessHours(request.getOpeningTime(), request.getClosingTime());
        }
    }
} 