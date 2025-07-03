package com.emsp.chargingstation.repository;

import com.emsp.chargingstation.domain.entity.Connector;
import com.emsp.chargingstation.domain.enums.ConnectorStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConnectorRepository extends JpaRepository<Connector, UUID> {
    
    /**
     * Find connectors by EVSE ID
     */
    List<Connector> findByEvseIdOrderByStandard(UUID evseId);
    
    /**
     * Find connectors by standard
     */
    List<Connector> findByStandardOrderByMaxPowerKwDesc(ConnectorStandard standard);
    
    /**
     * Find connectors with power greater than or equal to specified value
     */
    @Query("SELECT c FROM Connector c WHERE c.maxPowerKw >= :minPower ORDER BY c.maxPowerKw DESC")
    List<Connector> findByMinPower(@Param("minPower") BigDecimal minPower);
    
    /**
     * Count connectors by EVSE ID
     */
    long countByEvseId(UUID evseId);
} 