package com.emsp.chargingstation.service;

import com.emsp.chargingstation.domain.entity.Evse;
import com.emsp.chargingstation.domain.entity.Location;
import com.emsp.chargingstation.domain.enums.EvseStatus;
import com.emsp.chargingstation.domain.event.EvseStatusChangedEvent;
import com.emsp.chargingstation.domain.valueobject.EvseId;
import com.emsp.chargingstation.dto.request.CreateEvseRequest;
import com.emsp.chargingstation.dto.request.UpdateEvseStatusRequest;
import com.emsp.chargingstation.dto.response.EvseResponse;
import com.emsp.chargingstation.exception.DuplicateResourceException;
import com.emsp.chargingstation.exception.InvalidEvseStatusTransitionException;
import com.emsp.chargingstation.exception.ResourceNotFoundException;
import com.emsp.chargingstation.mapper.EvseMapper;
import com.emsp.chargingstation.repository.EvseRepository;
import com.emsp.chargingstation.repository.LocationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EvseService {
    
    private final EvseRepository evseRepository;
    private final LocationRepository locationRepository;
    private final EvseMapper evseMapper;
    private final ApplicationEventPublisher eventPublisher;
    
    public EvseService(
        EvseRepository evseRepository,
        LocationRepository locationRepository,
        EvseMapper evseMapper,
        ApplicationEventPublisher eventPublisher
    ) {
        this.evseRepository = evseRepository;
        this.locationRepository = locationRepository;
        this.evseMapper = evseMapper;
        this.eventPublisher = eventPublisher;
    }
    
    public EvseResponse createEvse(UUID locationId, CreateEvseRequest request) {
        // Check if location exists
        Location location = findLocationById(locationId);
        
        // Check if EVSE ID already exists
        EvseId evseId = EvseId.of(request.getEvseId());
        if (evseRepository.existsByEvseId(evseId)) {
            throw new DuplicateResourceException("EVSE", request.getEvseId());
        }
        
        // Create EVSE
        Evse evse = evseMapper.toEntity(request);
        evse.setLocation(location);
        
        // Set initial status to AVAILABLE (business rule)
        evse.changeStatus(EvseStatus.AVAILABLE);
        
        Evse savedEvse = evseRepository.save(evse);
        return evseMapper.toResponse(savedEvse);
    }
    
    @Transactional(readOnly = true)
    public EvseResponse getEvseById(UUID evseId) {
        Evse evse = findEvseById(evseId);
        return evseMapper.toResponse(evse);
    }
    
    @Transactional(readOnly = true)
    public EvseResponse getEvseByEvseId(String evseId) {
        EvseId evseIdentifier = EvseId.of(evseId);
        Evse evse = evseRepository.findByEvseId(evseIdentifier)
            .orElseThrow(() -> new ResourceNotFoundException("EVSE", evseId));
        return evseMapper.toResponse(evse);
    }
    
    @Transactional(readOnly = true)
    public List<EvseResponse> getEvsesByLocationId(UUID locationId) {
        // Verify location exists
        findLocationById(locationId);
        
        List<Evse> evses = evseRepository.findByLocationIdOrderByEvseId(locationId);
        return evseMapper.toResponseList(evses);
    }
    
    @Transactional(readOnly = true)
    public Page<EvseResponse> getEvsesByStatus(EvseStatus status, Pageable pageable) {
        Page<Evse> evses = evseRepository.findByStatusOrderByLastUpdatedDesc(status, pageable);
        return evses.map(evseMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<EvseResponse> getEvsesUpdatedAfter(Instant lastUpdated, Pageable pageable) {
        Page<Evse> evses = evseRepository.findByLastUpdatedAfterWithLocation(lastUpdated, pageable);
        return evses.map(evseMapper::toResponse);
    }
    
    public EvseResponse updateEvseStatus(UUID evseId, UpdateEvseStatusRequest request) {
        Evse evse = findEvseById(evseId);
        EvseStatus oldStatus = evse.getStatus();
        EvseStatus newStatus = request.getStatus();
        
        try {
            evse.changeStatus(newStatus);
        } catch (IllegalStateException e) {
            throw new InvalidEvseStatusTransitionException(oldStatus, newStatus);
        }
        
        Evse updatedEvse = evseRepository.save(evse);
        
        // Publish domain event
        EvseStatusChangedEvent event = new EvseStatusChangedEvent(
            updatedEvse.getId(),
            updatedEvse.getEvseId().getValue(),
            oldStatus,
            newStatus
        );
        eventPublisher.publishEvent(event);
        
        return evseMapper.toResponse(updatedEvse);
    }
    
    public void deleteEvse(UUID evseId) {
        Evse evse = findEvseById(evseId);
        evseRepository.delete(evse);
    }
    
    private Evse findEvseById(UUID evseId) {
        return evseRepository.findById(evseId)
            .orElseThrow(() -> new ResourceNotFoundException("EVSE", evseId.toString()));
    }
    
    private Location findLocationById(UUID locationId) {
        return locationRepository.findById(locationId)
            .orElseThrow(() -> new ResourceNotFoundException("Location", locationId.toString()));
    }
} 