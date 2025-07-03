package com.emsp.chargingstation.domain.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;

@Embeddable
public class EvseId {
    
    @NotBlank(message = "EVSE ID cannot be blank")
    @Pattern(
        regexp = "^[A-Z]{2}\\*[A-Z0-9]{3}\\*[A-Z0-9]{1,30}$",
        message = "EVSE ID must follow the format: <CountryCode>*<PartyID>*<LocalEVSEID>"
    )
    private String value;
    
    // JPA requires default constructor
    protected EvseId() {
    }
    
    private EvseId(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid EVSE ID format: " + value);
        }
        this.value = value;
    }
    
    public static EvseId of(String value) {
        return new EvseId(value);
    }
    
    public static EvseId of(String countryCode, String partyId, String localEvseId) {
        if (countryCode == null || countryCode.length() != 2 || !countryCode.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("Country code must be 2 uppercase letters");
        }
        if (partyId == null || partyId.length() != 3 || !partyId.matches("[A-Z0-9]{3}")) {
            throw new IllegalArgumentException("Party ID must be 3 alphanumeric characters");
        }
        if (localEvseId == null || localEvseId.length() == 0 || localEvseId.length() > 30 
            || !localEvseId.matches("[A-Z0-9]+")) {
            throw new IllegalArgumentException("Local EVSE ID must be 1-30 alphanumeric characters");
        }
        
        String value = countryCode + "*" + partyId + "*" + localEvseId;
        return new EvseId(value);
    }
    
    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        return value.matches("^[A-Z]{2}\\*[A-Z0-9]{3}\\*[A-Z0-9]{1,30}$");
    }
    
    public String getValue() {
        return value;
    }
    
    public String getCountryCode() {
        return value.split("\\*")[0];
    }
    
    public String getPartyId() {
        return value.split("\\*")[1];
    }
    
    public String getLocalEvseId() {
        return value.split("\\*")[2];
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvseId evseId = (EvseId) o;
        return Objects.equals(value, evseId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
} 