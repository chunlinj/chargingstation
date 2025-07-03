package com.emsp.chargingstation.domain.enums;

/**
 * Standard connector types for electric vehicle charging
 */
public enum ConnectorStandard {
    CCS1("Combined Charging System 1"),
    CCS2("Combined Charging System 2"), 
    CHADEMO("CHAdeMO"),
    TYPE_1("Type 1 (SAE J1772)"),
    TYPE_2("Type 2 (IEC 62196)"),
    TYPE_3("Type 3 (Scame)"),
    TESLA_SUPERCHARGER("Tesla Supercharger"),
    TESLA_DESTINATION("Tesla Destination"),
    GBT_AC("GB/T AC"),
    GBT_DC("GB/T DC");
    
    private final String description;
    
    ConnectorStandard(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 