package com.emsp.chargingstation.mapper;

import com.emsp.chargingstation.domain.entity.Connector;
import com.emsp.chargingstation.dto.request.CreateConnectorRequest;
import com.emsp.chargingstation.dto.response.ConnectorResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConnectorMapper {
    
    public Connector toEntity(CreateConnectorRequest request) {
        Connector connector = new Connector(
            request.getStandard(),
            request.getMaxPowerKw(),
            request.getVoltage(),
            request.getAmperage()
        );
        connector.setDescription(request.getDescription());
        return connector;
    }
    
    public ConnectorResponse toResponse(Connector connector) {
        ConnectorResponse response = new ConnectorResponse();
        response.setId(connector.getId());
        response.setStandard(connector.getStandard());
        response.setMaxPowerKw(connector.getMaxPowerKw());
        response.setVoltage(connector.getVoltage());
        response.setAmperage(connector.getAmperage());
        response.setDescription(connector.getDescription());
        response.setCreatedAt(connector.getCreatedAt());
        response.setLastUpdated(connector.getLastUpdated());
        return response;
    }
    
    public List<ConnectorResponse> toResponseList(List<Connector> connectors) {
        return connectors.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
} 