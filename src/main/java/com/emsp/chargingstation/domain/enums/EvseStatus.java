package com.emsp.chargingstation.domain.enums;

import java.util.Set;

public enum EvseStatus {
    INITIAL,
    AVAILABLE,
    BLOCKED,
    INOPERATIVE,
    REMOVED;
    
    /**
     * Check if transition from current status to target status is valid
     */
    public boolean canTransitionTo(EvseStatus targetStatus) {
        return getValidTransitions().contains(targetStatus);
    }
    
    /**
     * Get all valid transition states from current status
     */
    public Set<EvseStatus> getValidTransitions() {
        return switch (this) {
            case INITIAL -> Set.of(AVAILABLE);
            case AVAILABLE -> Set.of(BLOCKED, INOPERATIVE, REMOVED);
            case BLOCKED -> Set.of(AVAILABLE, REMOVED);
            case INOPERATIVE -> Set.of(AVAILABLE, REMOVED);
            case REMOVED -> Set.of(); // Terminal state, no transitions allowed
        };
    }
    
    /**
     * Check if the status is terminal (no further transitions allowed)
     */
    public boolean isTerminal() {
        return this == REMOVED;
    }
    
    /**
     * Check if the status is operational (can serve customers)
     */
    public boolean isOperational() {
        return this == AVAILABLE;
    }
} 