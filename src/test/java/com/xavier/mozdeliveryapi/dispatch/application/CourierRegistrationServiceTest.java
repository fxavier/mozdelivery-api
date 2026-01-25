package com.xavier.mozdeliveryapi.dispatch.application;

import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierApprovalRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierRegistrationRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierRegistrationResponse;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.CourierRegistrationServiceImpl;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.CourierProfileRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.CourierProfile;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.AvailabilitySchedule;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryCapacity;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.VehicleInfo;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.UserRepository;
import com.xavier.mozdeliveryapi.shared.domain.entity.User;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourierRegistrationServiceTest {
    
    @Mock
    private CourierProfileRepository courierProfileRepository;
    
    @Mock
    private UserRepository userRepository;
    
    private CourierRegistrationServiceImpl courierRegistrationService;
    
    @BeforeEach
    void setUp() {
        courierRegistrationService = new CourierRegistrationServiceImpl(
            courierProfileRepository, userRepository);
    }
    
    @Test
    void shouldRegisterCourierSuccessfully() {
        // Given
        CourierRegistrationRequest request = createValidRegistrationRequest();
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(courierProfileRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(courierProfileRepository.save(any(CourierProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        CourierRegistrationResponse response = courierRegistrationService.registerCourier(request);
        
        // Then
        assertThat(response.userId()).isNotNull();
        assertThat(response.deliveryPersonId()).isNotNull();
        assertThat(response.email()).isEqualTo(request.email());
        assertThat(response.fullName()).isEqualTo(request.getFullName());
        assertThat(response.approvalStatus()).isEqualTo(CourierApprovalStatus.PENDING);
        assertThat(response.message()).contains("Registration submitted successfully");
        
        verify(userRepository).save(any(User.class));
        verify(courierProfileRepository).save(any(CourierProfile.class));
    }
    
    @Test
    void shouldFailRegistrationWhenEmailAlreadyExists() {
        // Given
        CourierRegistrationRequest request = createValidRegistrationRequest();
        User existingUser = new User(UserId.generate(), request.email(), "Existing", "User", 
                                   "+258123456789", UserRole.CLIENT, null);
        
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));
        
        // When
        CourierRegistrationResponse response = courierRegistrationService.registerCourier(request);
        
        // Then
        assertThat(response.userId()).isNull();
        assertThat(response.message()).contains("Email already registered");
    }
    
    @Test
    void shouldApproveCourierSuccessfully() {
        // Given
        DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
        CourierProfile courierProfile = createCourierProfile(deliveryPersonId);
        CourierApprovalRequest approvalRequest = new CourierApprovalRequest(
            deliveryPersonId, CourierApprovalStatus.APPROVED, "Approved", "All documents verified");
        
        when(courierProfileRepository.findById(deliveryPersonId)).thenReturn(Optional.of(courierProfile));
        when(courierProfileRepository.save(any(CourierProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        CourierProfile result = courierRegistrationService.processApproval(approvalRequest);
        
        // Then
        assertThat(result.getApprovalStatus()).isEqualTo(CourierApprovalStatus.APPROVED);
        assertThat(result.canWork()).isTrue();
        verify(courierProfileRepository).save(courierProfile);
    }
    
    @Test
    void shouldRejectCourierSuccessfully() {
        // Given
        DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
        CourierProfile courierProfile = createCourierProfile(deliveryPersonId);
        CourierApprovalRequest approvalRequest = new CourierApprovalRequest(
            deliveryPersonId, CourierApprovalStatus.REJECTED, "Rejected", "Invalid documents");
        
        when(courierProfileRepository.findById(deliveryPersonId)).thenReturn(Optional.of(courierProfile));
        when(courierProfileRepository.save(any(CourierProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        CourierProfile result = courierRegistrationService.processApproval(approvalRequest);
        
        // Then
        assertThat(result.getApprovalStatus()).isEqualTo(CourierApprovalStatus.REJECTED);
        assertThat(result.canWork()).isFalse();
        verify(courierProfileRepository).save(courierProfile);
    }
    
    @Test
    void shouldThrowExceptionWhenCourierNotFound() {
        // Given
        DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
        when(courierProfileRepository.findById(deliveryPersonId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> courierRegistrationService.getCourierProfile(deliveryPersonId))
            .hasMessageContaining("Courier not found");
    }
    
    private CourierRegistrationRequest createValidRegistrationRequest() {
        VehicleInfo vehicleInfo = new VehicleInfo("MOTORCYCLE", "Honda", "CB125", "ABC-123", "Red", 2020);
        DeliveryCapacity capacity = new DeliveryCapacity(5, 20, 50);
        Location location = Location.of(-25.9692, 32.5732); // Maputo coordinates
        
        AvailabilitySchedule.TimeSlot timeSlot = new AvailabilitySchedule.TimeSlot(
            LocalTime.of(8, 0), LocalTime.of(18, 0));
        AvailabilitySchedule schedule = new AvailabilitySchedule(
            Map.of(DayOfWeek.MONDAY, timeSlot, DayOfWeek.TUESDAY, timeSlot),
            Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY)
        );
        
        return new CourierRegistrationRequest(
            "John", "Doe", "john.doe@example.com", "+258123456789",
            vehicleInfo, capacity, location, schedule, "Maputo",
            "DL123456", "Jane Doe", "+258987654321", "Experienced rider"
        );
    }
    
    private CourierProfile createCourierProfile(DeliveryPersonId deliveryPersonId) {
        CourierRegistrationRequest request = createValidRegistrationRequest();
        return new CourierProfile(
            deliveryPersonId,
            UserId.generate(),
            com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId.generate(),
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phoneNumber(),
            request.vehicleInfo(),
            request.deliveryCapacity(),
            request.initialLocation(),
            request.availabilitySchedule(),
            request.city(),
            request.drivingLicenseNumber(),
            request.emergencyContactName(),
            request.emergencyContactPhone(),
            request.notes()
        );
    }
}