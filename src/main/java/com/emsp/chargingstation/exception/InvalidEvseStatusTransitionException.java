package com.emsp.chargingstation.exception;

import com.emsp.chargingstation.domain.enums.EvseStatus;

public class InvalidEvseStatusTransitionException extends ChargingStationException {
    
    public InvalidEvseStatusTransitionException(EvseStatus currentStatus, EvseStatus targetStatus) {
        super(
            String.format("Cannot transition EVSE from %s to %s", currentStatus, targetStatus),
            "INVALID_STATUS_TRANSITION"
        );
    }
} 