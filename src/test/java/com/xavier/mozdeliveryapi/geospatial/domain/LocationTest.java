package com.xavier.mozdeliveryapi.geospatial.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;

class LocationTest {
    
    @Test
    void shouldCreateValidLocation() {
        // Given
        BigDecimal latitude = BigDecimal.valueOf(-25.9692);
        BigDecimal longitude = BigDecimal.valueOf(32.5732);
        
        // When
        Location location = Location.of(latitude, longitude);
        
        // Then
        assertThat(location.getLatitude()).isEqualTo(latitude.setScale(8, java.math.RoundingMode.HALF_UP));
        assertThat(location.getLongitude()).isEqualTo(longitude.setScale(8, java.math.RoundingMode.HALF_UP));
    }
    
    @Test
    void shouldCreateLocationFromDoubles() {
        // Given
        double latitude = -25.9692;
        double longitude = 32.5732;
        
        // When
        Location location = Location.of(latitude, longitude);
        
        // Then
        assertThat(location.getLatitude().doubleValue()).isEqualTo(latitude);
        assertThat(location.getLongitude().doubleValue()).isEqualTo(longitude);
    }
    
    @Test
    void shouldThrowExceptionForInvalidLatitude() {
        // Given
        BigDecimal invalidLatitude = BigDecimal.valueOf(91.0);
        BigDecimal validLongitude = BigDecimal.valueOf(32.5732);
        
        // When & Then
        assertThatThrownBy(() -> Location.of(invalidLatitude, validLongitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude must be between -90 and 90 degrees");
    }
    
    @Test
    void shouldThrowExceptionForInvalidLongitude() {
        // Given
        BigDecimal validLatitude = BigDecimal.valueOf(-25.9692);
        BigDecimal invalidLongitude = BigDecimal.valueOf(181.0);
        
        // When & Then
        assertThatThrownBy(() -> Location.of(validLatitude, invalidLongitude))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude must be between -180 and 180 degrees");
    }
    
    @Test
    void shouldCalculateDistanceBetweenLocations() {
        // Given - Maputo and Beira coordinates
        Location maputo = Location.of(-25.9692, 32.5732);
        Location beira = Location.of(-19.8436, 34.8389);
        
        // When
        Distance distance = maputo.distanceTo(beira);
        
        // Then
        assertThat(distance.getKilometers().doubleValue()).isGreaterThan(600); // Approximate distance
        assertThat(distance.getKilometers().doubleValue()).isLessThan(800);
    }
    
    @Test
    void shouldCalculateZeroDistanceForSameLocation() {
        // Given
        Location location1 = Location.of(-25.9692, 32.5732);
        Location location2 = Location.of(-25.9692, 32.5732);
        
        // When
        Distance distance = location1.distanceTo(location2);
        
        // Then
        assertThat(distance.getMeters().doubleValue()).isEqualTo(0.0);
    }
    
    @Test
    void shouldBeEqualForSameCoordinates() {
        // Given
        Location location1 = Location.of(-25.9692, 32.5732);
        Location location2 = Location.of(-25.9692, 32.5732);
        
        // When & Then
        assertThat(location1).isEqualTo(location2);
        assertThat(location1.hashCode()).isEqualTo(location2.hashCode());
    }
    
    @Test
    void shouldHaveProperToString() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        
        // When
        String toString = location.toString();
        
        // Then
        assertThat(toString).contains("Location");
        assertThat(toString).contains("-25.9692");
        assertThat(toString).contains("32.5732");
    }
}
