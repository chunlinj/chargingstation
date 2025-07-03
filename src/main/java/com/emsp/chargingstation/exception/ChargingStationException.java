package com.emsp.chargingstation.exception;

public class ChargingStationException extends RuntimeException {
    
    private final String errorCode;
    
    public ChargingStationException(String message) {
        super(message);
        this.errorCode = "GENERIC_ERROR";
    }
    
    public ChargingStationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ChargingStationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERIC_ERROR";
    }
    
    public ChargingStationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
} 