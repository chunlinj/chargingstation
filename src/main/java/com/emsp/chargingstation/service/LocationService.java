package com.emsp.chargingstation.service;

import com.emsp.chargingstation.domain.entity.Location;
import com.emsp.chargingstation.domain.event.LocationCreatedEvent;
import com.emsp.chargingstation.dto.request.CreateLocationRequest;
import com.emsp.chargingstation.dto.request.UpdateLocationRequest;
import com.emsp.chargingstation.dto.response.LocationResponse;
import com.emsp.chargingstation.exception.ResourceNotFoundException;
import com.emsp.chargingstation.mapper.LocationMapper;
import com.emsp.chargingstation.repository.LocationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class LocationService {
    
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final ApplicationEventPublisher eventPublisher;
    
    public LocationService(
        LocationRepository locationRepository,
        LocationMapper locationMapper,
        ApplicationEventPublisher eventPublisher
    ) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.eventPublisher = eventPublisher;
    }
    
    public LocationResponse createLocation(CreateLocationRequest request) {
        Location location = locationMapper.toEntity(request);
        Location savedLocation = locationRepository.save(location);
        
        // Publish domain event
        LocationCreatedEvent event = new LocationCreatedEvent(
            savedLocation.getId(),
            savedLocation.getName(),
            savedLocation.getAddress()
        );
        eventPublisher.publishEvent(event);
        
        return locationMapper.toResponse(savedLocation);
    }
    
    @Transactional(readOnly = true)
    public LocationResponse getLocationById(UUID locationId) {
        Location location = findLocationByIdWithEvses(locationId);
        return locationMapper.toResponse(location);
    }
    
    @Transactional(readOnly = true)
    public Page<LocationResponse> getLocationsUpdatedAfter(Instant lastUpdated, Pageable pageable) {
        Page<Location> locations = locationRepository.findByLastUpdatedAfterOrderByLastUpdatedAsc(lastUpdated, pageable);
        return locations.map(locationMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<LocationResponse> getAllLocations(Pageable pageable) {
        Page<Location> locations = locationRepository.findAll(pageable);
        return locations.map(locationMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<LocationResponse> searchLocationsByName(String name, Pageable pageable) {
        Page<Location> locations = locationRepository.findByNameContainingIgnoreCaseOrderByName(name, pageable);
        return locations.map(locationMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<LocationResponse> getLocationsInBounds(
        Double minLatitude, Double maxLatitude,
        Double minLongitude, Double maxLongitude,
        Pageable pageable
    ) {
        Page<Location> locations = locationRepository.findLocationsInBounds(
            minLatitude, maxLatitude, minLongitude, maxLongitude, pageable
        );
        return locations.map(locationMapper::toResponse);
    }
    
    public LocationResponse updateLocation(UUID locationId, UpdateLocationRequest request) {
        Location location = findLocationById(locationId);
        locationMapper.updateEntity(location, request);
        Location updatedLocation = locationRepository.save(location);
        return locationMapper.toResponse(updatedLocation);
    }
    
    public void deleteLocation(UUID locationId) {
        Location location = findLocationById(locationId);
        locationRepository.delete(location);
    }
    
    private Location findLocationById(UUID locationId) {
        return locationRepository.findById(locationId)
            .orElseThrow(() -> new ResourceNotFoundException("Location", locationId.toString()));
    }
    
    private Location findLocationByIdWithEvses(UUID locationId) {
        return locationRepository.findWithEvsesByIdEquals(locationId)
            .orElseThrow(() -> new ResourceNotFoundException("Location", locationId.toString()));
    }
} 