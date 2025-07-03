package com.emsp.chargingstation.repository;

import com.emsp.chargingstation.domain.entity.Evse;
import com.emsp.chargingstation.domain.enums.EvseStatus;
import com.emsp.chargingstation.domain.valueobject.EvseId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvseRepository extends JpaRepository<Evse, UUID> {
    
    /**
     * Find EVSE by its unique EVSE ID
     */
    Optional<Evse> findByEvseId(EvseId evseId);
    
    /**
     * Check if an EVSE with given EVSE ID exists
     */
    boolean existsByEvseId(EvseId evseId);
    
    /**
     * Find EVSEs by location ID
     */
    List<Evse> findByLocationIdOrderByEvseId(UUID locationId);
    
    /**
     * Find EVSEs by status
     */
    Page<Evse> findByStatusOrderByLastUpdatedDesc(EvseStatus status, Pageable pageable);
    
    /**
     * Find EVSEs updated after a specific timestamp
     */
    @Query("SELECT e FROM Evse e JOIN FETCH e.location WHERE e.lastUpdated > :lastUpdated ORDER BY e.lastUpdated ASC")
    Page<Evse> findByLastUpdatedAfterWithLocation(@Param("lastUpdated") Instant lastUpdated, Pageable pageable);
    
    /**
     * Count EVSEs by status for a specific location
     */
    @Query("SELECT COUNT(e) FROM Evse e WHERE e.location.id = :locationId AND e.status = :status")
    long countByLocationIdAndStatus(@Param("locationId") UUID locationId, @Param("status") EvseStatus status);
} 