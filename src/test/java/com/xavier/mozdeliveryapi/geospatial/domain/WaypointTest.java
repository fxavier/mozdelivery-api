package com.xavier.mozdeliveryapi.geospatial.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

class WaypointTest {
    
    @Test
    void shouldCreateBasicWaypoint() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        WaypointType type = WaypointType.START;
        
        // When
        Waypoint waypoint = Waypoint.of(location, type);
        
        // Then
        assertThat(waypoint.getLocation()).isEqualTo(location);
        assertThat(waypoint.getType()).isEqualTo(type);
        assertThat(waypoint.getStopDuration()).isEmpty();
        assertThat(waypoint.getDescription()).isEmpty();
    }
    
    @Test
    void shouldCreateWaypointWithStopDuration() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        WaypointType type = WaypointType.DELIVERY;
        Duration stopDuration = Duration.ofMinutes(10);
        
        // When
        Waypoint waypoint = Waypoint.of(location, type, stopDuration);
        
        // Then
        assertThat(waypoint.getLocation()).isEqualTo(location);
        assertThat(waypoint.getType()).isEqualTo(type);
        assertThat(waypoint.getStopDuration()).contains(stopDuration);
        assertThat(waypoint.getDescription()).isEmpty();
    }
    
    @Test
    void shouldCreateWaypointWithDescription() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        WaypointType type = WaypointType.DELIVERY;
        Duration stopDuration = Duration.ofMinutes(5);
        String description = "Deliver package to apartment 3B";
        
        // When
        Waypoint waypoint = Waypoint.of(location, type, stopDuration, description);
        
        // Then
        assertThat(waypoint.getLocation()).isEqualTo(location);
        assertThat(waypoint.getType()).isEqualTo(type);
        assertThat(waypoint.getStopDuration()).contains(stopDuration);
        assertThat(waypoint.getDescription()).contains(description);
    }
    
    @Test
    void shouldCreateStartWaypoint() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        
        // When
        Waypoint waypoint = Waypoint.start(location);
        
        // Then
        assertThat(waypoint.getType()).isEqualTo(WaypointType.START);
        assertThat(waypoint.isStart()).isTrue();
        assertThat(waypoint.isEnd()).isFalse();
        assertThat(waypoint.isDelivery()).isFalse();
    }
    
    @Test
    void shouldCreateEndWaypoint() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        
        // When
        Waypoint waypoint = Waypoint.end(location);
        
        // Then
        assertThat(waypoint.getType()).isEqualTo(WaypointType.END);
        assertThat(waypoint.isEnd()).isTrue();
        assertThat(waypoint.isStart()).isFalse();
        assertThat(waypoint.isDelivery()).isFalse();
    }
    
    @Test
    void shouldCreateDeliveryWaypoint() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        Duration stopDuration = Duration.ofMinutes(8);
        String description = "Customer delivery";
        
        // When
        Waypoint waypoint = Waypoint.delivery(location, stopDuration, description);
        
        // Then
        assertThat(waypoint.getType()).isEqualTo(WaypointType.DELIVERY);
        assertThat(waypoint.isDelivery()).isTrue();
        assertThat(waypoint.isStart()).isFalse();
        assertThat(waypoint.isEnd()).isFalse();
        assertThat(waypoint.requiresStop()).isTrue();
    }
    
    @Test
    void shouldCheckIfRequiresStop() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        Waypoint waypointWithStop = Waypoint.of(location, WaypointType.DELIVERY, Duration.ofMinutes(5));
        Waypoint waypointWithoutStop = Waypoint.of(location, WaypointType.START);
        Waypoint waypointWithZeroStop = Waypoint.of(location, WaypointType.INTERMEDIATE, Duration.ZERO);
        
        // When & Then
        assertThat(waypointWithStop.requiresStop()).isTrue();
        assertThat(waypointWithoutStop.requiresStop()).isFalse();
        assertThat(waypointWithZeroStop.requiresStop()).isFalse();
    }
    
    @Test
    void shouldBeEqualForSameProperties() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        Duration stopDuration = Duration.ofMinutes(5);
        String description = "Test delivery";
        
        Waypoint waypoint1 = Waypoint.of(location, WaypointType.DELIVERY, stopDuration, description);
        Waypoint waypoint2 = Waypoint.of(location, WaypointType.DELIVERY, stopDuration, description);
        
        // When & Then
        assertThat(waypoint1).isEqualTo(waypoint2);
        assertThat(waypoint1.hashCode()).isEqualTo(waypoint2.hashCode());
    }
    
    @Test
    void shouldHaveProperToString() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        Duration stopDuration = Duration.ofMinutes(5);
        String description = "Test delivery";
        
        Waypoint waypoint = Waypoint.of(location, WaypointType.DELIVERY, stopDuration, description);
        
        // When
        String toString = waypoint.toString();
        
        // Then
        assertThat(toString).contains("Waypoint{");
        assertThat(toString).contains("type=DELIVERY");
        assertThat(toString).contains("location=");
        assertThat(toString).contains("stopDuration=");
        assertThat(toString).contains("description='Test delivery'");
    }
    
    @Test
    void shouldThrowExceptionForNullLocation() {
        // When & Then
        assertThatThrownBy(() -> Waypoint.of(null, WaypointType.START))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Location cannot be null");
    }
    
    @Test
    void shouldThrowExceptionForNullType() {
        // Given
        Location location = Location.of(-25.9692, 32.5732);
        
        // When & Then
        assertThatThrownBy(() -> Waypoint.of(location, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Waypoint type cannot be null");
    }
}