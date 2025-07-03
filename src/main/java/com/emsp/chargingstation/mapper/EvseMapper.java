package com.emsp.chargingstation.mapper;

import com.emsp.chargingstation.domain.entity.Evse;
import com.emsp.chargingstation.domain.valueobject.EvseId;
import com.emsp.chargingstation.dto.request.CreateEvseRequest;
import com.emsp.chargingstation.dto.response.EvseResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EvseMapper {
    
    private final ConnectorMapper connectorMapper;
    
    public EvseMapper(ConnectorMapper connectorMapper) {
        this.connectorMapper = connectorMapper;
    }
    
    public Evse toEntity(CreateEvseRequest request) {
        EvseId evseId = EvseId.of(request.getEvseId());
        Evse evse = new Evse(evseId);
        evse.setDescription(request.getDescription());
        return evse;
    }
    
    public EvseResponse toResponse(Evse evse) {
        EvseResponse response = new EvseResponse();
        response.setId(evse.getId());
        response.setEvseId(evse.getEvseId().getValue());
        response.setStatus(evse.getStatus());
        response.setDescription(evse.getDescription());
        response.setCreatedAt(evse.getCreatedAt());
        response.setLastUpdated(evse.getLastUpdated());
        
        if (evse.getConnectors() != null && !evse.getConnectors().isEmpty()) {
            response.setConnectors(
                evse.getConnectors().stream()
                    .map(connectorMapper::toResponse)
                    .collect(Collectors.toSet())
            );
        }
        
        return response;
    }
    
    public List<EvseResponse> toResponseList(List<Evse> evses) {
        return evses.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
} 