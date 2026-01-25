package com.xavier.mozdeliveryapi.dispatch.domain;

import com.xavier.mozdeliveryapi.dispatch.domain.entity.CourierProfile;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.AvailabilitySchedule;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryCapacity;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.VehicleInfo;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourierProfileTest {
    
    @Test
    void shouldCreateCourierProfileWithPendingStatus() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        
        // Then
        assertThat(courierProfile.getApprovalStatus()).isEqualTo(CourierApprovalStatus.PENDING);
        assertThat(courierProfile.getStatus()).isEqualTo(DeliveryPersonStatus.INACTIVE);
        assertThat(courierProfile.canWork()).isFalse();
        assertThat(courierProfile.isAvailable()).isFalse();
    }
    
    @Test
    void shouldApproveCourierSuccessfully() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        
        // When
        courierProfile.approve("All documents verified", "Approved by admin");
        
        // Then
        assertThat(courierProfile.getApprovalStatus()).isEqualTo(CourierApprovalStatus.APPROVED);
        assertThat(courierProfile.getStatus()).isEqualTo(DeliveryPersonStatus.OFF_DUTY);
        assertThat(courierProfile.canWork()).isTrue();
        assertThat(courierProfile.getReviewNotes()).isEqualTo("All documents verified");
        assertThat(courierProfile.getReviewerComments()).isEqualTo("Approved by admin");
        assertThat(courierProfile.getApprovedAt()).isNotNull();
    }
    
    @Test
    void shouldRejectCourierSuccessfully() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        
        // When
        courierProfile.reject("Invalid documents", "Documents not clear");
        
        // Then
        assertThat(courierProfile.getApprovalStatus()).isEqualTo(CourierApprovalStatus.REJECTED);
        assertThat(courierProfile.getStatus()).isEqualTo(DeliveryPersonStatus.INACTIVE);
        assertThat(courierProfile.canWork()).isFalse();
        assertThat(courierProfile.getReviewNotes()).isEqualTo("Invalid documents");
        assertThat(courierProfile.getReviewerComments()).isEqualTo("Documents not clear");
    }
    
    @Test
    void shouldSuspendApprovedCourier() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        courierProfile.approve("Approved", "Initial approval");
        
        // When
        courierProfile.suspend("Policy violation");
        
        // Then
        assertThat(courierProfile.getApprovalStatus()).isEqualTo(CourierApprovalStatus.SUSPENDED);
        assertThat(courierProfile.getStatus()).isEqualTo(DeliveryPersonStatus.INACTIVE);
        assertThat(courierProfile.canWork()).isFalse();
        assertThat(courierProfile.getReviewNotes()).isEqualTo("Policy violation");
    }
    
    @Test
    void shouldUpdateVehicleInfo() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        VehicleInfo newVehicleInfo = new VehicleInfo("CAR", "Toyota", "Corolla", "XYZ-789", "Blue", 2022);
        DeliveryCapacity newCapacity = new DeliveryCapacity(10, 50, 100);
        
        // When
        courierProfile.updateVehicleInfo(newVehicleInfo, newCapacity);
        
        // Then
        assertThat(courierProfile.getVehicleInfo()).isEqualTo(newVehicleInfo);
        assertThat(courierProfile.getCapacity()).isEqualTo(newCapacity);
    }
    
    @Test
    void shouldUpdateAvailabilitySchedule() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        AvailabilitySchedule.TimeSlot newTimeSlot = new AvailabilitySchedule.TimeSlot(
            LocalTime.of(9, 0), LocalTime.of(17, 0));
        AvailabilitySchedule newSchedule = new AvailabilitySchedule(
            Map.of(DayOfWeek.WEDNESDAY, newTimeSlot),
            Set.of(DayOfWeek.WEDNESDAY)
        );
        
        // When
        courierProfile.updateAvailabilitySchedule(newSchedule);
        
        // Then
        assertThat(courierProfile.getAvailabilitySchedule()).isEqualTo(newSchedule);
    }
    
    @Test
    void shouldNotAllowStatusChangeWhenNotApproved() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        
        // When & Then
        assertThatThrownBy(() -> courierProfile.updateStatus(DeliveryPersonStatus.AVAILABLE))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot change status - courier not approved");
    }
    
    @Test
    void shouldAllowStatusChangeWhenApproved() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        courierProfile.approve("Approved", "All good");
        
        // When
        courierProfile.updateStatus(DeliveryPersonStatus.AVAILABLE);
        
        // Then
        assertThat(courierProfile.getStatus()).isEqualTo(DeliveryPersonStatus.AVAILABLE);
        assertThat(courierProfile.isAvailable()).isTrue();
    }
    
    @Test
    void shouldNotAcceptDeliveryWhenNotApproved() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        
        // When & Then
        assertThat(courierProfile.canAcceptDelivery(5, 10)).isFalse();
        
        assertThatThrownBy(() -> courierProfile.assignDelivery(5, 10))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot accept delivery - not approved");
    }
    
    @Test
    void shouldAcceptDeliveryWhenApprovedAndAvailable() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        courierProfile.approve("Approved", "All good");
        courierProfile.updateStatus(DeliveryPersonStatus.AVAILABLE);
        
        // When
        boolean canAccept = courierProfile.canAcceptDelivery(5, 10);
        
        // Then
        assertThat(canAccept).isTrue();
        
        // When assigning delivery
        courierProfile.assignDelivery(5, 10);
        
        // Then
        assertThat(courierProfile.getCurrentOrderCount()).isEqualTo(1);
        assertThat(courierProfile.getCurrentWeight()).isEqualTo(5);
        assertThat(courierProfile.getCurrentVolume()).isEqualTo(10);
    }
    
    @Test
    void shouldCompleteDeliverySuccessfully() {
        // Given
        CourierProfile courierProfile = createCourierProfile();
        courierProfile.approve("Approved", "All good");
        courierProfile.updateStatus(DeliveryPersonStatus.AVAILABLE);
        courierProfile.assignDelivery(5, 10);
        
        // When
        courierProfile.completeDelivery(5, 10);
        
        // Then
        assertThat(courierProfile.getCurrentOrderCount()).isEqualTo(0);
        assertThat(courierProfile.getCurrentWeight()).isEqualTo(0);
        assertThat(courierProfile.getCurrentVolume()).isEqualTo(0);
    }
    
    @Test
    void shouldValidateRequiredFields() {
        // When & Then
        assertThatThrownBy(() -> new CourierProfile(
            null, UserId.generate(), TenantId.generate(),
            "John", "Doe", "john@example.com", "+258123456789",
            createVehicleInfo(), createDeliveryCapacity(), createLocation(),
            createAvailabilitySchedule(), "Maputo", null, null, null, null
        )).hasMessageContaining("Delivery person ID cannot be null");
        
        assertThatThrownBy(() -> new CourierProfile(
            DeliveryPersonId.generate(), null, TenantId.generate(),
            "John", "Doe", "john@example.com", "+258123456789",
            createVehicleInfo(), createDeliveryCapacity(), createLocation(),
            createAvailabilitySchedule(), "Maputo", null, null, null, null
        )).hasMessageContaining("User ID cannot be null");
        
        assertThatThrownBy(() -> new CourierProfile(
            DeliveryPersonId.generate(), UserId.generate(), TenantId.generate(),
            "", "Doe", "john@example.com", "+258123456789",
            createVehicleInfo(), createDeliveryCapacity(), createLocation(),
            createAvailabilitySchedule(), "Maputo", null, null, null, null
        )).hasMessageContaining("First name cannot be null or empty");
    }
    
    private CourierProfile createCourierProfile() {
        return new CourierProfile(
            DeliveryPersonId.generate(),
            UserId.generate(),
            TenantId.generate(),
            "John",
            "Doe",
            "john.doe@example.com",
            "+258123456789",
            createVehicleInfo(),
            createDeliveryCapacity(),
            createLocation(),
            createAvailabilitySchedule(),
            "Maputo",
            "DL123456",
            "Jane Doe",
            "+258987654321",
            "Experienced rider"
        );
    }
    
    private VehicleInfo createVehicleInfo() {
        return new VehicleInfo("MOTORCYCLE", "Honda", "CB125", "ABC-123", "Red", 2020);
    }
    
    private DeliveryCapacity createDeliveryCapacity() {
        return new DeliveryCapacity(5, 20, 50);
    }
    
    private Location createLocation() {
        return Location.of(-25.9692, 32.5732); // Maputo coordinates
    }
    
    private AvailabilitySchedule createAvailabilitySchedule() {
        AvailabilitySchedule.TimeSlot timeSlot = new AvailabilitySchedule.TimeSlot(
            LocalTime.of(8, 0), LocalTime.of(18, 0));
        return new AvailabilitySchedule(
            Map.of(DayOfWeek.MONDAY, timeSlot, DayOfWeek.TUESDAY, timeSlot),
            Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
        );
    }
}