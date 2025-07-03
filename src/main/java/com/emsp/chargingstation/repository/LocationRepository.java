package com.emsp.chargingstation.repository;

import com.emsp.chargingstation.domain.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    
    /**
     * Find location by ID with evses and connectors loaded
     */
    @EntityGraph(attributePaths = {"evses", "evses.connectors"})
    Optional<Location> findWithEvsesByIdEquals(UUID id);
    
    /**
     * Find locations updated after a specific timestamp with pagination
     */
    Page<Location> findByLastUpdatedAfterOrderByLastUpdatedAsc(Instant lastUpdated, Pageable pageable);
    
    /**
     * Find locations by name containing given text (case insensitive)
     */
    Page<Location> findByNameContainingIgnoreCaseOrderByName(String name, Pageable pageable);
    
    /**
     * Find locations within a geographic bounding box
     */
    @Query("SELECT l FROM Location l WHERE " +
           "l.latitude BETWEEN :minLat AND :maxLat AND " +
           "l.longitude BETWEEN :minLon AND :maxLon " +
           "ORDER BY l.name")
    Page<Location> findLocationsInBounds(
        @Param("minLat") Double minLatitude,
        @Param("maxLat") Double maxLatitude,
        @Param("minLon") Double minLongitude,
        @Param("maxLon") Double maxLongitude,
        Pageable pageable
    );
} 