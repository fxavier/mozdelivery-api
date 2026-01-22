package com.xavier.mozdeliveryapi.dispatch.domain;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.geospatial.domain.Location;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

class DeliveryPersonTest {
    
    @Test
    void shouldCreateDeliveryPersonWithValidData() {
        // Given
        DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
        TenantId tenantId = TenantId.generate();
        String name = "John Doe";
        String phoneNumber = "+258123456789";
        String vehicleType = "Motorcycle";
        DeliveryCapacity capacity = DeliveryCapacity.defaultCapacity();
        Location initialLocation = Location.of(BigDecimal.valueOf(-25.9692), BigDecimal.valueOf(32.5732));
        
        // When
        DeliveryPerson deliveryPerson = new DeliveryPerson(deliveryPersonId, tenantId, name, 
                                                          phoneNumber, vehicleType, capacity, initialLocation);
        
        // Then
        assertThat(deliveryPerson.getDeliveryPersonId()).isEqualTo(deliveryPersonId);
        assertThat(deliveryPerson.getTenantId()).isEqualTo(tenantId);
        assertThat(deliveryPerson.getName()).isEqualTo(name);
        assertThat(deliveryPerson.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(deliveryPerson.getVehicleType()).isEqualTo(vehicleType);
        assertThat(deliveryPerson.getCapacity()).isEqualTo(capacity);
        assertThat(deliveryPerson.getCurrentLocation()).isEqualTo(initialLocation);
        assertThat(deliveryPerson.getStatus()).isEqualTo(DeliveryPersonStatus.AVAILABLE);
        assertThat(deliveryPerson.getCurrentOrderCount()).isEqualTo(0);
        assertThat(deliveryPerson.isAvailable()).isTrue();
    }
    
    @Test
    void shouldNotCreateDeliveryPersonWithBlankName() {
        // Given
        DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
        TenantId tenantId = TenantId.generate();
        String blankName = "   ";
        String phoneNumber = "+258123456789";
        String vehicleType = "Motorcycle";
        DeliveryCapacity capacity = DeliveryCapacity.defaultCapacity();
        Location initialLocation = Location.of(BigDecimal.valueOf(-25.9692), BigDecimal.valueOf(32.5732));
        
        // When/Then
        assertThatThrownBy(() -> new DeliveryPerson(deliveryPersonId, tenantId, blankName, 
                                                   phoneNumber, vehicleType, capacity, initialLocation))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Name cannot be blank");
    }
    
    @Test
    void shouldAcceptDeliveryWhenAvailableAndWithinCapacity() {
        // Given
        DeliveryPerson deliveryPerson = createTestDeliveryPerson();
        
        // When
        boolean canAccept = deliveryPerson.canAcceptDelivery(2000, 5000);
        
        // Then
        assertThat(canAccept).isTrue();
    }
    
    @Test
    void shouldNotAcceptDeliveryWhenExceedsCapacity() {
        // Given
        DeliveryPerson deliveryPerson = createTestDeliveryPerson();
        
        // When
        boolean canAccept = deliveryPerson.canAcceptDelivery(15000, 60000); // Exceeds capacity
        
        // Then
        assertThat(canAccept).isFalse();
    }
    
    @Test
    void shouldAssignDeliveryAndUpdateCapacity() {
        // Given
        DeliveryPerson deliveryPerson = createTestDeliveryPerson();
        int orderWeight = 2000;
        int orderVolume = 5000;
        
        // When
        deliveryPerson.assignDelivery(orderWeight, orderVolume);
        
        // Then
        assertThat(deliveryPerson.getCurrentOrderCount()).isEqualTo(1);
        assertThat(deliveryPerson.getCurrentWeight()).isEqualTo(orderWeight);
        assertThat(deliveryPerson.getCurrentVolume()).isEqualTo(orderVolume);
        assertThat(deliveryPerson.getCapacityUtilization()).isGreaterThan(0.0);
    }
    
    @Test
    void shouldCompleteDeliveryAndFreeCapacity() {
        // Given
        DeliveryPerson deliveryPerson = createTestDeliveryPerson();
        int orderWeight = 2000;
        int orderVolume = 5000;
        deliveryPerson.assignDelivery(orderWeight, orderVolume);
        
        // When
        deliveryPerson.completeDelivery(orderWeight, orderVolume);
        
        // Then
        assertThat(deliveryPerson.getCurrentOrderCount()).isEqualTo(0);
        assertThat(deliveryPerson.getCurrentWeight()).isEqualTo(0);
        assertThat(deliveryPerson.getCurrentVolume()).isEqualTo(0);
        assertThat(deliveryPerson.getCapacityUtilization()).isEqualTo(0.0);
    }
    
    @Test
    void shouldUpdateStatus() {
        // Given
        DeliveryPerson deliveryPerson = createTestDeliveryPerson();
        
        // When
        deliveryPerson.updateStatus(DeliveryPersonStatus.ON_BREAK);
        
        // Then
        assertThat(deliveryPerson.getStatus()).isEqualTo(DeliveryPersonStatus.ON_BREAK);
        assertThat(deliveryPerson.isAvailable()).isFalse();
    }
    
    @Test
    void shouldUpdateLocation() {
        // Given
        DeliveryPerson deliveryPerson = createTestDeliveryPerson();
        Location newLocation = Location.of(BigDecimal.valueOf(-25.9680), BigDecimal.valueOf(32.5800));
        
        // When
        deliveryPerson.updateLocation(newLocation);
        
        // Then
        assertThat(deliveryPerson.getCurrentLocation()).isEqualTo(newLocation);
    }
    
    private DeliveryPerson createTestDeliveryPerson() {
        DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
        TenantId tenantId = TenantId.generate();
        String name = "John Doe";
        String phoneNumber = "+258123456789";
        String vehicleType = "Motorcycle";
        DeliveryCapacity capacity = DeliveryCapacity.defaultCapacity();
        Location initialLocation = Location.of(BigDecimal.valueOf(-25.9692), BigDecimal.valueOf(32.5732));
        
        return new DeliveryPerson(deliveryPersonId, tenantId, name, phoneNumber, 
                                 vehicleType, capacity, initialLocation);
    }
}