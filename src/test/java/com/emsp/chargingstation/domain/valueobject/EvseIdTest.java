package com.emsp.chargingstation.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EVSE ID Value Object Tests")
class EvseIdTest {
    
    @Test
    @DisplayName("Should create valid EVSE ID from string")
    void shouldCreateValidEvseIdFromString() {
        // Given
        String validEvseId = "US*ABC*EVSE123456";
        
        // When
        EvseId evseId = EvseId.of(validEvseId);
        
        // Then
        assertNotNull(evseId);
        assertEquals(validEvseId, evseId.getValue());
        assertEquals("US", evseId.getCountryCode());
        assertEquals("ABC", evseId.getPartyId());
        assertEquals("EVSE123456", evseId.getLocalEvseId());
    }
    
    @Test
    @DisplayName("Should create valid EVSE ID from components")
    void shouldCreateValidEvseIdFromComponents() {
        // Given
        String countryCode = "CN";
        String partyId = "XYZ";
        String localEvseId = "STATION001";
        
        // When
        EvseId evseId = EvseId.of(countryCode, partyId, localEvseId);
        
        // Then
        assertNotNull(evseId);
        assertEquals("CN*XYZ*STATION001", evseId.getValue());
        assertEquals(countryCode, evseId.getCountryCode());
        assertEquals(partyId, evseId.getPartyId());
        assertEquals(localEvseId, evseId.getLocalEvseId());
    }
    
    @ParameterizedTest
    @DisplayName("Should reject invalid EVSE ID formats")
    @ValueSource(strings = {
        "US*ABC",           // Missing local ID
        "US*ABCD*EVSE123",  // Party ID too long
        "USA*ABC*EVSE123",  // Country code too long
        "us*ABC*EVSE123",   // Lowercase country code
        "US*abc*EVSE123",   // Lowercase party ID
        "US*AB*EVSE123",    // Party ID too short
        "U*ABC*EVSE123",    // Country code too short
        "US-ABC-EVSE123",   // Wrong separator
        "",                 // Empty string
        "US*ABC*"           // Empty local ID
    })
    void shouldRejectInvalidEvseIdFormats(String invalidEvseId) {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> EvseId.of(invalidEvseId));
    }
    
    @Test
    @DisplayName("Should reject local ID that is too long")
    void shouldRejectLocalIdThatIsTooLong() {
        // Given
        String tooLongLocalId = "US*ABC*" + "A".repeat(31);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> EvseId.of(tooLongLocalId));
    }
    
    @Test
    @DisplayName("Should validate EVSE ID format correctly")
    void shouldValidateEvseIdFormat() {
        // Valid cases
        assertTrue(EvseId.isValid("US*ABC*EVSE123456"));
        assertTrue(EvseId.isValid("NL*XYZ*1"));
        assertTrue(EvseId.isValid("CN*123*" + "A".repeat(30)));
        
        // Invalid cases
        assertFalse(EvseId.isValid(null));
        assertFalse(EvseId.isValid(""));
        assertFalse(EvseId.isValid("US*ABC"));
        assertFalse(EvseId.isValid("us*ABC*EVSE123"));
        assertFalse(EvseId.isValid("US*ABCD*EVSE123"));
        assertFalse(EvseId.isValid("US*ABC*" + "A".repeat(31)));
    }
    
    @Test
    @DisplayName("Should handle equality correctly")
    void shouldHandleEqualityCorrectly() {
        // Given
        EvseId evseId1 = EvseId.of("US*ABC*EVSE123");
        EvseId evseId2 = EvseId.of("US*ABC*EVSE123");
        EvseId evseId3 = EvseId.of("US*ABC*EVSE456");
        
        // Then
        assertEquals(evseId1, evseId2);
        assertNotEquals(evseId1, evseId3);
        assertEquals(evseId1.hashCode(), evseId2.hashCode());
        assertNotEquals(evseId1.hashCode(), evseId3.hashCode());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid components")
    void shouldThrowExceptionForInvalidComponents() {
        // Invalid country code
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of("U", "ABC", "EVSE123"));
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of("usa", "ABC", "EVSE123"));
        
        // Invalid party ID
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of("US", "AB", "EVSE123"));
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of("US", "ABCD", "EVSE123"));
        
        // Invalid local EVSE ID
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of("US", "ABC", ""));
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of("US", "ABC", "A".repeat(31)));
        
        // Null components
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of(null, "ABC", "EVSE123"));
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of("US", null, "EVSE123"));
        assertThrows(IllegalArgumentException.class, 
            () -> EvseId.of("US", "ABC", null));
    }
} 