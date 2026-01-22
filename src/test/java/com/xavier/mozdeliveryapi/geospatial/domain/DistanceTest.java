package com.xavier.mozdeliveryapi.geospatial.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class DistanceTest {
    
    @Test
    void shouldCreateDistanceFromMeters() {
        // Given
        BigDecimal meters = BigDecimal.valueOf(1500.50);
        
        // When
        Distance distance = Distance.ofMeters(meters);
        
        // Then
        assertThat(distance.getMeters()).isEqualByComparingTo(BigDecimal.valueOf(1500.50));
        assertThat(distance.getKilometers()).isEqualByComparingTo(BigDecimal.valueOf(1.501));
    }
    
    @Test
    void shouldCreateDistanceFromKilometers() {
        // Given
        BigDecimal kilometers = BigDecimal.valueOf(2.5);
        
        // When
        Distance distance = Distance.ofKilometers(kilometers);
        
        // Then
        assertThat(distance.getKilometers()).isEqualByComparingTo(BigDecimal.valueOf(2.500));
        assertThat(distance.getMeters()).isEqualByComparingTo(BigDecimal.valueOf(2500.00));
    }
    
    @Test
    void shouldCreateZeroDistance() {
        // When
        Distance distance = Distance.zero();
        
        // Then
        assertThat(distance.getMeters()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(distance.getKilometers()).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldThrowExceptionForNegativeDistance() {
        // Given
        BigDecimal negativeMeters = BigDecimal.valueOf(-100);
        
        // When & Then
        assertThatThrownBy(() -> Distance.ofMeters(negativeMeters))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Distance cannot be negative");
    }
    
    @Test
    void shouldAddDistances() {
        // Given
        Distance distance1 = Distance.ofMeters(1000);
        Distance distance2 = Distance.ofMeters(500);
        
        // When
        Distance result = distance1.add(distance2);
        
        // Then
        assertThat(result.getMeters()).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
    }
    
    @Test
    void shouldSubtractDistances() {
        // Given
        Distance distance1 = Distance.ofMeters(1000);
        Distance distance2 = Distance.ofMeters(300);
        
        // When
        Distance result = distance1.subtract(distance2);
        
        // Then
        assertThat(result.getMeters()).isEqualByComparingTo(BigDecimal.valueOf(700.00));
    }
    
    @Test
    void shouldThrowExceptionWhenSubtractionResultsInNegative() {
        // Given
        Distance distance1 = Distance.ofMeters(300);
        Distance distance2 = Distance.ofMeters(1000);
        
        // When & Then
        assertThatThrownBy(() -> distance1.subtract(distance2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Result distance cannot be negative");
    }
    
    @Test
    void shouldMultiplyDistance() {
        // Given
        Distance distance = Distance.ofMeters(100);
        BigDecimal factor = BigDecimal.valueOf(2.5);
        
        // When
        Distance result = distance.multiply(factor);
        
        // Then
        assertThat(result.getMeters()).isEqualByComparingTo(BigDecimal.valueOf(250.00));
    }
    
    @Test
    void shouldCompareDistances() {
        // Given
        Distance smaller = Distance.ofMeters(100);
        Distance larger = Distance.ofMeters(200);
        Distance equal = Distance.ofMeters(100);
        
        // When & Then
        assertThat(smaller.isLessThan(larger)).isTrue();
        assertThat(larger.isGreaterThan(smaller)).isTrue();
        assertThat(smaller.isLessThan(equal)).isFalse();
        assertThat(smaller.isGreaterThan(equal)).isFalse();
    }
    
    @Test
    void shouldImplementComparable() {
        // Given
        Distance distance1 = Distance.ofMeters(100);
        Distance distance2 = Distance.ofMeters(200);
        Distance distance3 = Distance.ofMeters(100);
        
        // When & Then
        assertThat(distance1.compareTo(distance2)).isLessThan(0);
        assertThat(distance2.compareTo(distance1)).isGreaterThan(0);
        assertThat(distance1.compareTo(distance3)).isEqualTo(0);
    }
    
    @Test
    void shouldFormatToStringCorrectly() {
        // Given
        Distance meters = Distance.ofMeters(500);
        Distance kilometers = Distance.ofKilometers(2.5);
        
        // When
        String metersString = meters.toString();
        String kilometersString = kilometers.toString();
        
        // Then
        assertThat(metersString).isEqualTo("500 m");
        assertThat(kilometersString).isEqualTo("2.50 km");
    }
}