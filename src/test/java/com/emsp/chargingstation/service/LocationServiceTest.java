package com.emsp.chargingstation.service;

import com.emsp.chargingstation.domain.entity.Location;
import com.emsp.chargingstation.domain.event.LocationCreatedEvent;
import com.emsp.chargingstation.dto.request.CreateLocationRequest;
import com.emsp.chargingstation.dto.request.UpdateLocationRequest;
import com.emsp.chargingstation.dto.response.LocationResponse;
import com.emsp.chargingstation.exception.ResourceNotFoundException;
import com.emsp.chargingstation.mapper.LocationMapper;
import com.emsp.chargingstation.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Location Service Tests")
class LocationServiceTest {
    
    @Mock
    private LocationRepository locationRepository;
    
    @Mock
    private LocationMapper locationMapper;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @InjectMocks
    private LocationService locationService;
    
    private Location testLocation;
    private LocationResponse testLocationResponse;
    private CreateLocationRequest createRequest;
    private UpdateLocationRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testLocation = new Location(
            "Test Station",
            "123 Test Street, Test City",
            BigDecimal.valueOf(40.7128),
            BigDecimal.valueOf(-74.0060)
        );
        testLocation.setDescription("Test charging station");
        
        testLocationResponse = new LocationResponse();
        testLocationResponse.setId(UUID.randomUUID());
        testLocationResponse.setName("Test Station");
        testLocationResponse.setAddress("123 Test Street, Test City");
        testLocationResponse.setLatitude(BigDecimal.valueOf(40.7128));
        testLocationResponse.setLongitude(BigDecimal.valueOf(-74.0060));
        testLocationResponse.setDescription("Test charging station");
        
        createRequest = new CreateLocationRequest(
            "Test Station",
            "123 Test Street, Test City",
            BigDecimal.valueOf(40.7128),
            BigDecimal.valueOf(-74.0060)
        );
        createRequest.setDescription("Test charging station");
        createRequest.setOpeningTime(LocalTime.of(9, 0));
        createRequest.setClosingTime(LocalTime.of(17, 0));
        
        updateRequest = new UpdateLocationRequest();
        updateRequest.setName("Updated Station");
        updateRequest.setDescription("Updated description");
    }
    
    @Test
    @DisplayName("Should create location successfully")
    void shouldCreateLocationSuccessfully() {
        // Given
        when(locationMapper.toEntity(createRequest)).thenReturn(testLocation);
        when(locationRepository.save(testLocation)).thenReturn(testLocation);
        when(locationMapper.toResponse(testLocation)).thenReturn(testLocationResponse);
        
        // When
        LocationResponse result = locationService.createLocation(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(testLocationResponse.getName(), result.getName());
        verify(locationMapper).toEntity(createRequest);
        verify(locationRepository).save(testLocation);
        verify(locationMapper).toResponse(testLocation);
        verify(eventPublisher).publishEvent(any(LocationCreatedEvent.class));
    }
    
    @Test
    @DisplayName("Should get location by ID successfully")
    void shouldGetLocationByIdSuccessfully() {
        // Given
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findWithEvsesByIdEquals(locationId)).thenReturn(Optional.of(testLocation));
        when(locationMapper.toResponse(testLocation)).thenReturn(testLocationResponse);
        
        // When
        LocationResponse result = locationService.getLocationById(locationId);
        
        // Then
        assertNotNull(result);
        assertEquals(testLocationResponse.getName(), result.getName());
        verify(locationRepository).findWithEvsesByIdEquals(locationId);
        verify(locationMapper).toResponse(testLocation);
    }
    
    @Test
    @DisplayName("Should throw exception when location not found")
    void shouldThrowExceptionWhenLocationNotFound() {
        // Given
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findWithEvsesByIdEquals(locationId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> locationService.getLocationById(locationId));
        verify(locationRepository).findWithEvsesByIdEquals(locationId);
        verifyNoInteractions(locationMapper);
    }
    
    @Test
    @DisplayName("Should get all locations with pagination")
    void shouldGetAllLocationsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> locationPage = new PageImpl<>(List.of(testLocation));
        Page<LocationResponse> responsePage = new PageImpl<>(List.of(testLocationResponse));
        
        when(locationRepository.findAll(pageable)).thenReturn(locationPage);
        when(locationMapper.toResponse(testLocation)).thenReturn(testLocationResponse);
        
        // When
        Page<LocationResponse> result = locationService.getAllLocations(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testLocationResponse.getName(), result.getContent().get(0).getName());
        verify(locationRepository).findAll(pageable);
    }
    
    @Test
    @DisplayName("Should get locations updated after timestamp")
    void shouldGetLocationsUpdatedAfterTimestamp() {
        // Given
        Instant lastUpdated = Instant.now().minusSeconds(3600);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> locationPage = new PageImpl<>(List.of(testLocation));
        
        when(locationRepository.findByLastUpdatedAfterOrderByLastUpdatedAsc(lastUpdated, pageable))
            .thenReturn(locationPage);
        when(locationMapper.toResponse(testLocation)).thenReturn(testLocationResponse);
        
        // When
        Page<LocationResponse> result = locationService.getLocationsUpdatedAfter(lastUpdated, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(locationRepository).findByLastUpdatedAfterOrderByLastUpdatedAsc(lastUpdated, pageable);
    }
    
    @Test
    @DisplayName("Should update location successfully")
    void shouldUpdateLocationSuccessfully() {
        // Given
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(testLocation));
        when(locationRepository.save(testLocation)).thenReturn(testLocation);
        when(locationMapper.toResponse(testLocation)).thenReturn(testLocationResponse);
        
        // When
        LocationResponse result = locationService.updateLocation(locationId, updateRequest);
        
        // Then
        assertNotNull(result);
        verify(locationRepository).findById(locationId);
        verify(locationMapper).updateEntity(testLocation, updateRequest);
        verify(locationRepository).save(testLocation);
        verify(locationMapper).toResponse(testLocation);
    }
    
    @Test
    @DisplayName("Should delete location successfully")
    void shouldDeleteLocationSuccessfully() {
        // Given
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(testLocation));
        
        // When
        locationService.deleteLocation(locationId);
        
        // Then
        verify(locationRepository).findById(locationId);
        verify(locationRepository).delete(testLocation);
    }
    
    @Test
    @DisplayName("Should search locations by name")
    void shouldSearchLocationsByName() {
        // Given
        String searchTerm = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> locationPage = new PageImpl<>(List.of(testLocation));
        
        when(locationRepository.findByNameContainingIgnoreCaseOrderByName(searchTerm, pageable))
            .thenReturn(locationPage);
        when(locationMapper.toResponse(testLocation)).thenReturn(testLocationResponse);
        
        // When
        Page<LocationResponse> result = locationService.searchLocationsByName(searchTerm, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(locationRepository).findByNameContainingIgnoreCaseOrderByName(searchTerm, pageable);
    }
    
    @Test
    @DisplayName("Should get locations in bounds")
    void shouldGetLocationsInBounds() {
        // Given
        Double minLat = 40.0, maxLat = 41.0, minLon = -75.0, maxLon = -74.0;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> locationPage = new PageImpl<>(List.of(testLocation));
        
        when(locationRepository.findLocationsInBounds(minLat, maxLat, minLon, maxLon, pageable))
            .thenReturn(locationPage);
        when(locationMapper.toResponse(testLocation)).thenReturn(testLocationResponse);
        
        // When
        Page<LocationResponse> result = locationService.getLocationsInBounds(
            minLat, maxLat, minLon, maxLon, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(locationRepository).findLocationsInBounds(minLat, maxLat, minLon, maxLon, pageable);
    }
} 