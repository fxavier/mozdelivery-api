package com.xavier.mozdeliveryapi.geospatial.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import com.xavier.mozdeliveryapi.geospatial.domain.entity.DistanceCalculationService;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Route;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.WaypointType;
import com.xavier.mozdeliveryapi.geospatial.infra.persistence.DistanceCalculationServiceImpl;

class RouteTest {
    
    private DistanceCalculationService distanceService;
    
    @BeforeEach
    void setUp() {
        distanceService = new DistanceCalculationServiceImpl();
    }
    
    @Test
    void shouldCreateRouteFromLocations() {
        // Given
        Location maputo = Location.of(-25.9692, 32.5732);
        Location beira = Location.of(-19.8436, 34.8389);
        List<Location> locations = List.of(maputo, beira);
        
        // When
        Route route = Route.fromLocations(locations, distanceService);
        
        // Then
        assertThat(route.getWaypointCount()).isEqualTo(2);
        assertThat(route.getStartLocation()).isEqualTo(maputo);
        assertThat(route.getEndLocation()).isEqualTo(beira);
        assertThat(route.getTotalDistance().getKilometers().doubleValue()).isGreaterThan(600);
        assertThat(route.getEstimatedDuration()).isGreaterThan(Duration.ofHours(20));
    }
    
    @Test
    void shouldCreateRouteWithMultipleWaypoints() {
        // Given
        Location start = Location.of(-25.9692, 32.5732); // Maputo
        Location middle = Location.of(-23.8647, 35.0794); // Inhambane
        Location end = Location.of(-19.8436, 34.8389);   // Beira
        List<Location> locations = List.of(start, middle, end);
        
        // When
        Route route = Route.fromLocations(locations, distanceService);
        
        // Then
        assertThat(route.getWaypointCount()).isEqualTo(3);
        assertThat(route.getWaypoints().get(0).getType()).isEqualTo(WaypointType.START);
        assertThat(route.getWaypoints().get(1).getType()).isEqualTo(WaypointType.INTERMEDIATE);
        assertThat(route.getWaypoints().get(2).getType()).isEqualTo(WaypointType.END);
    }
    
    @Test
    void shouldThrowExceptionForInsufficientWaypoints() {
        // Given
        Location singleLocation = Location.of(-25.9692, 32.5732);
        List<Location> locations = List.of(singleLocation);
        
        // When & Then
        assertThatThrownBy(() -> Route.fromLocations(locations, distanceService))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 2 locations");
    }
    
    @Test
    void shouldCheckIfRouteContainsLocation() {
        // Given
        Location maputo = Location.of(-25.9692, 32.5732);
        Location beira = Location.of(-19.8436, 34.8389);
        Location nampula = Location.of(-15.1165, 39.2666);
        List<Location> locations = List.of(maputo, beira);
        Route route = Route.fromLocations(locations, distanceService);
        
        // When & Then
        assertThat(route.containsLocation(maputo)).isTrue();
        assertThat(route.containsLocation(beira)).isTrue();
        assertThat(route.containsLocation(nampula)).isFalse();
    }
    
    @Test
    void shouldCalculateAverageSpeed() {
        // Given
        Location start = Location.of(-25.9692, 32.5732);
        Location end = Location.of(-25.9792, 32.5832); // Very close location
        List<Location> locations = List.of(start, end);
        Route route = Route.fromLocations(locations, distanceService);
        
        // When
        double averageSpeed = route.getAverageSpeedKmh();
        
        // Then
        assertThat(averageSpeed).isPositive();
        // The speed calculation is based on distance and estimated duration
        // Just verify it's a reasonable speed for city driving
        assertThat(averageSpeed).isBetween(20.0, 60.0);
    }
    
    @Test
    void shouldGetAllLocationsInOrder() {
        // Given
        Location start = Location.of(-25.9692, 32.5732);
        Location middle = Location.of(-23.8647, 35.0794);
        Location end = Location.of(-19.8436, 34.8389);
        List<Location> originalLocations = List.of(start, middle, end);
        Route route = Route.fromLocations(originalLocations, distanceService);
        
        // When
        List<Location> routeLocations = route.getLocations();
        
        // Then
        assertThat(routeLocations).hasSize(3);
        assertThat(routeLocations).containsExactlyElementsOf(originalLocations);
    }
}
