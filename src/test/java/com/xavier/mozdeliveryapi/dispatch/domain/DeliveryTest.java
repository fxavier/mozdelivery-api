package com.xavier.mozdeliveryapi.dispatch.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Route;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Waypoint;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.WaypointType;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryStatus;

class DeliveryTest {
    
    @Test
    void shouldCreateDeliveryWithValidData() {
        // Given
        DeliveryId deliveryId = DeliveryId.generate();
        TenantId tenantId = TenantId.generate();
        OrderId orderId = OrderId.generate();
        DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
        
        Location pickupLocation = Location.of(BigDecimal.valueOf(-25.9692), BigDecimal.valueOf(32.5732));
        Location deliveryLocation = Location.of(BigDecimal.valueOf(-25.9662), BigDecimal.valueOf(32.5892));
        
        List<Waypoint> waypoints = List.of(
            Waypoint.of(pickupLocation, WaypointType.START),
            Waypoint.of(deliveryLocation, WaypointType.END)
        );
        
        Route route = Route.of(waypoints, Distance.ofKilometers(5), Duration.ofMinutes(15));
        
        // When
        Delivery delivery = new Delivery(deliveryId, tenantId, orderId, deliveryPersonId, 
                                       route, 1000, 2000);
        
        // Then
        assertThat(delivery.getDeliveryId()).isEqualTo(deliveryId);
        assertThat(delivery.getTenantId()).isEqualTo(tenantId);
        assertThat(delivery.getOrderId()).isEqualTo(orderId);
        assertThat(delivery.getDeliveryPersonId()).isEqualTo(deliveryPersonId);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
        assertThat(delivery.getOrderWeight()).isEqualTo(1000);
        assertThat(delivery.getOrderVolume()).isEqualTo(2000);
        assertThat(delivery.getProgress()).isEqualTo(0.0);
        assertThat(delivery.getCurrentLocation()).isEqualTo(pickupLocation);
    }
    
    @Test
    void shouldUpdateDeliveryStatus() {
        // Given
        Delivery delivery = createTestDelivery();
        
        // When
        delivery.updateStatus(DeliveryStatus.EN_ROUTE_TO_PICKUP);
        
        // Then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.EN_ROUTE_TO_PICKUP);
        assertThat(delivery.getProgress()).isEqualTo(0.2);
    }
    
    @Test
    void shouldNotAllowInvalidStatusTransition() {
        // Given
        Delivery delivery = createTestDelivery();
        
        // When/Then
        assertThatThrownBy(() -> delivery.updateStatus(DeliveryStatus.DELIVERED))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from ASSIGNED to DELIVERED");
    }
    
    @Test
    void shouldUpdateLocation() {
        // Given
        Delivery delivery = createTestDelivery();
        Location newLocation = Location.of(BigDecimal.valueOf(-25.9680), BigDecimal.valueOf(32.5800));
        
        // When
        delivery.updateLocation(newLocation);
        
        // Then
        assertThat(delivery.getCurrentLocation()).isEqualTo(newLocation);
    }
    
    @Test
    void shouldCancelDelivery() {
        // Given
        Delivery delivery = createTestDelivery();
        String reason = "Customer not available";
        
        // When
        delivery.cancel(reason);
        
        // Then
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
    }
    
    @Test
    void shouldNotCancelCompletedDelivery() {
        // Given
        Delivery delivery = createTestDelivery();
        delivery.updateStatus(DeliveryStatus.EN_ROUTE_TO_PICKUP);
        delivery.updateStatus(DeliveryStatus.ARRIVED_AT_PICKUP);
        delivery.updateStatus(DeliveryStatus.IN_TRANSIT);
        delivery.updateStatus(DeliveryStatus.ARRIVED_AT_DELIVERY);
        delivery.updateStatus(DeliveryStatus.DELIVERED);
        
        // When/Then
        assertThatThrownBy(() -> delivery.cancel("Test reason"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot cancel completed delivery");
    }
    
    private Delivery createTestDelivery() {
        DeliveryId deliveryId = DeliveryId.generate();
        TenantId tenantId = TenantId.generate();
        OrderId orderId = OrderId.generate();
        DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
        
        Location pickupLocation = Location.of(BigDecimal.valueOf(-25.9692), BigDecimal.valueOf(32.5732));
        Location deliveryLocation = Location.of(BigDecimal.valueOf(-25.9662), BigDecimal.valueOf(32.5892));
        
        List<Waypoint> waypoints = List.of(
            Waypoint.of(pickupLocation, WaypointType.START),
            Waypoint.of(deliveryLocation, WaypointType.END)
        );
        
        Route route = Route.of(waypoints, Distance.ofKilometers(5), Duration.ofMinutes(15));
        
        return new Delivery(deliveryId, tenantId, orderId, deliveryPersonId, 
                          route, 1000, 2000);
    }
}
