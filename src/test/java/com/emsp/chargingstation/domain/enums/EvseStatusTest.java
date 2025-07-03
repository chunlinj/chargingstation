package com.emsp.chargingstation.domain.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EVSE Status Tests")
class EvseStatusTest {
    
    @Test
    @DisplayName("Should allow valid transitions from INITIAL")
    void shouldAllowValidTransitionsFromInitial() {
        // Given
        EvseStatus initial = EvseStatus.INITIAL;
        
        // Then
        assertTrue(initial.canTransitionTo(EvseStatus.AVAILABLE));
        assertFalse(initial.canTransitionTo(EvseStatus.BLOCKED));
        assertFalse(initial.canTransitionTo(EvseStatus.INOPERATIVE));
        assertFalse(initial.canTransitionTo(EvseStatus.REMOVED));
        assertFalse(initial.canTransitionTo(EvseStatus.INITIAL));
    }
    
    @Test
    @DisplayName("Should allow valid transitions from AVAILABLE")
    void shouldAllowValidTransitionsFromAvailable() {
        // Given
        EvseStatus available = EvseStatus.AVAILABLE;
        
        // Then
        assertTrue(available.canTransitionTo(EvseStatus.BLOCKED));
        assertTrue(available.canTransitionTo(EvseStatus.INOPERATIVE));
        assertTrue(available.canTransitionTo(EvseStatus.REMOVED));
        assertFalse(available.canTransitionTo(EvseStatus.INITIAL));
        assertFalse(available.canTransitionTo(EvseStatus.AVAILABLE));
    }
    
    @Test
    @DisplayName("Should allow valid transitions from BLOCKED")
    void shouldAllowValidTransitionsFromBlocked() {
        // Given
        EvseStatus blocked = EvseStatus.BLOCKED;
        
        // Then
        assertTrue(blocked.canTransitionTo(EvseStatus.AVAILABLE));
        assertTrue(blocked.canTransitionTo(EvseStatus.REMOVED));
        assertFalse(blocked.canTransitionTo(EvseStatus.INITIAL));
        assertFalse(blocked.canTransitionTo(EvseStatus.BLOCKED));
        assertFalse(blocked.canTransitionTo(EvseStatus.INOPERATIVE));
    }
    
    @Test
    @DisplayName("Should allow valid transitions from INOPERATIVE")
    void shouldAllowValidTransitionsFromInoperative() {
        // Given
        EvseStatus inoperative = EvseStatus.INOPERATIVE;
        
        // Then
        assertTrue(inoperative.canTransitionTo(EvseStatus.AVAILABLE));
        assertTrue(inoperative.canTransitionTo(EvseStatus.REMOVED));
        assertFalse(inoperative.canTransitionTo(EvseStatus.INITIAL));
        assertFalse(inoperative.canTransitionTo(EvseStatus.BLOCKED));
        assertFalse(inoperative.canTransitionTo(EvseStatus.INOPERATIVE));
    }
    
    @Test
    @DisplayName("Should not allow any transitions from REMOVED")
    void shouldNotAllowAnyTransitionsFromRemoved() {
        // Given
        EvseStatus removed = EvseStatus.REMOVED;
        
        // Then
        assertFalse(removed.canTransitionTo(EvseStatus.INITIAL));
        assertFalse(removed.canTransitionTo(EvseStatus.AVAILABLE));
        assertFalse(removed.canTransitionTo(EvseStatus.BLOCKED));
        assertFalse(removed.canTransitionTo(EvseStatus.INOPERATIVE));
        assertFalse(removed.canTransitionTo(EvseStatus.REMOVED));
    }
    
    @Test
    @DisplayName("Should identify terminal status correctly")
    void shouldIdentifyTerminalStatusCorrectly() {
        assertTrue(EvseStatus.REMOVED.isTerminal());
        assertFalse(EvseStatus.INITIAL.isTerminal());
        assertFalse(EvseStatus.AVAILABLE.isTerminal());
        assertFalse(EvseStatus.BLOCKED.isTerminal());
        assertFalse(EvseStatus.INOPERATIVE.isTerminal());
    }
    
    @Test
    @DisplayName("Should identify operational status correctly")
    void shouldIdentifyOperationalStatusCorrectly() {
        assertTrue(EvseStatus.AVAILABLE.isOperational());
        assertFalse(EvseStatus.INITIAL.isOperational());
        assertFalse(EvseStatus.BLOCKED.isOperational());
        assertFalse(EvseStatus.INOPERATIVE.isOperational());
        assertFalse(EvseStatus.REMOVED.isOperational());
    }
} 
 