package com.emsp.chargingstation.service;

import com.emsp.chargingstation.domain.entity.Connector;
import com.emsp.chargingstation.domain.entity.Evse;
import com.emsp.chargingstation.domain.valueobject.EvseId;
import com.emsp.chargingstation.dto.request.CreateConnectorRequest;
import com.emsp.chargingstation.dto.response.ConnectorResponse;
import com.emsp.chargingstation.exception.ResourceNotFoundException;
import com.emsp.chargingstation.mapper.ConnectorMapper;
import com.emsp.chargingstation.repository.ConnectorRepository;
import com.emsp.chargingstation.repository.EvseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ConnectorService {
    
    private final ConnectorRepository connectorRepository;
    private final EvseRepository evseRepository;
    private final ConnectorMapper connectorMapper;
    
    public ConnectorService(
        ConnectorRepository connectorRepository,
        EvseRepository evseRepository,
        ConnectorMapper connectorMapper
    ) {
        this.connectorRepository = connectorRepository;
        this.evseRepository = evseRepository;
        this.connectorMapper = connectorMapper;
    }
    
    public ConnectorResponse createConnector(UUID evseId, CreateConnectorRequest request) {
        // Check if EVSE exists
        Evse evse = findEvseById(evseId);
        
        // Create connector
        Connector connector = connectorMapper.toEntity(request);
        connector.setEvse(evse);
        
        Connector savedConnector = connectorRepository.save(connector);
        System.out.println("Connector created successfully");
        return connectorMapper.toResponse(savedConnector);
    }
    
    public ConnectorResponse createConnectorByEvseId(String evseId, CreateConnectorRequest request) {
        // Check if EVSE exists by EVSE identifier
        Evse evse = findEvseByEvseId(evseId);
        
        // Create connector
        Connector connector = connectorMapper.toEntity(request);
        connector.setEvse(evse);
        
        Connector savedConnector = connectorRepository.save(connector);
        return connectorMapper.toResponse(savedConnector);
    }
    
    @Transactional(readOnly = true)
    public ConnectorResponse getConnectorById(UUID connectorId) {
        Connector connector = findConnectorById(connectorId);
        return connectorMapper.toResponse(connector);
    }
    
    @Transactional(readOnly = true)
    public List<ConnectorResponse> getConnectorsByEvseId(UUID evseId) {
        // Verify EVSE exists
        findEvseById(evseId);
        
        List<Connector> connectors = connectorRepository.findByEvseIdOrderByStandard(evseId);
        return connectorMapper.toResponseList(connectors);
    }
    
    @Transactional(readOnly = true)
    public List<ConnectorResponse> getConnectorsByEvseIdentifier(String evseId) {
        // Verify EVSE exists and get its UUID
        Evse evse = findEvseByEvseId(evseId);
        
        List<Connector> connectors = connectorRepository.findByEvseIdOrderByStandard(evse.getId());
        return connectorMapper.toResponseList(connectors);
    }
    
    public void deleteConnector(UUID connectorId) {
        Connector connector = findConnectorById(connectorId);
        connectorRepository.delete(connector);
    }
    
    private Connector findConnectorById(UUID connectorId) {
        return connectorRepository.findById(connectorId)
            .orElseThrow(() -> new ResourceNotFoundException("Connector", connectorId.toString()));
    }
    
    private Evse findEvseById(UUID evseId) {
        return evseRepository.findById(evseId)
            .orElseThrow(() -> new ResourceNotFoundException("EVSE", evseId.toString()));
    }
    
    private Evse findEvseByEvseId(String evseId) {
        EvseId evseIdentifier = EvseId.of(evseId);
        return evseRepository.findByEvseId(evseIdentifier)
            .orElseThrow(() -> new ResourceNotFoundException("EVSE", evseId));
    }
} 