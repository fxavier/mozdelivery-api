package com.xavier.mozdeliveryapi.dispatch.application.usecase;

import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierApprovalRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierRegistrationRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.CourierRegistrationResponse;
import com.xavier.mozdeliveryapi.dispatch.application.dto.UpdateAvailabilityRequest;
import com.xavier.mozdeliveryapi.dispatch.application.dto.UpdateVehicleInfoRequest;
import com.xavier.mozdeliveryapi.dispatch.application.usecase.port.CourierProfileRepository;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.CourierProfile;
import com.xavier.mozdeliveryapi.dispatch.domain.exception.DeliveryNotFoundException;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.UserRepository;
import com.xavier.mozdeliveryapi.shared.domain.entity.User;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of courier registration service.
 */
@Service
@Transactional
public class CourierRegistrationServiceImpl implements CourierRegistrationService {
    
    private final CourierProfileRepository courierProfileRepository;
    private final UserRepository userRepository;
    
    public CourierRegistrationServiceImpl(CourierProfileRepository courierProfileRepository,
                                         UserRepository userRepository) {
        this.courierProfileRepository = Objects.requireNonNull(courierProfileRepository, 
                                                              "Courier profile repository cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "User repository cannot be null");
    }
    
    @Override
    public CourierRegistrationResponse registerCourier(CourierRegistrationRequest request) {
        Objects.requireNonNull(request, "Registration request cannot be null");
        
        try {
            // Check if email is already registered
            userRepository.findByEmail(request.email())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Email already registered: " + request.email());
                });
            
            // Check if phone number is already in use
            courierProfileRepository.findByPhoneNumber(request.phoneNumber())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Phone number already in use: " + request.phoneNumber());
                });
            
            // Create user account
            UserId userId = UserId.generate();
            User user = new User(
                userId,
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                UserRole.COURIER,
                null // Couriers don't belong to a specific merchant
            );
            User savedUser = userRepository.save(user);
            
            // Create courier profile
            DeliveryPersonId deliveryPersonId = DeliveryPersonId.generate();
            CourierProfile courierProfile = new CourierProfile(
                deliveryPersonId,
                savedUser.getUserId(),
                TenantId.generate(), // Platform-level tenant for couriers
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
            
            CourierProfile savedProfile = courierProfileRepository.save(courierProfile);
            
            return CourierRegistrationResponse.success(
                savedUser.getUserId(),
                savedProfile.getDeliveryPersonId(),
                savedProfile.getEmail(),
                savedProfile.getFullName(),
                savedProfile.getPhoneNumber(),
                savedProfile.getCity(),
                savedProfile.getCreatedAt()
            );
            
        } catch (Exception e) {
            return CourierRegistrationResponse.failure("Registration failed: " + e.getMessage());
        }
    }
    
    @Override
    public CourierProfile processApproval(CourierApprovalRequest request) {
        Objects.requireNonNull(request, "Approval request cannot be null");
        
        CourierProfile courierProfile = courierProfileRepository.findById(request.deliveryPersonId())
            .orElseThrow(() -> new DeliveryNotFoundException("Courier not found: " + request.deliveryPersonId()));
        
        switch (request.approvalStatus()) {
            case APPROVED -> courierProfile.approve(request.reviewNotes(), request.reviewerComments());
            case REJECTED -> courierProfile.reject(request.reviewNotes(), request.reviewerComments());
            default -> throw new IllegalArgumentException("Invalid approval status: " + request.approvalStatus());
        }
        
        return courierProfileRepository.save(courierProfile);
    }
    
    @Override
    public CourierProfile updateVehicleInfo(UpdateVehicleInfoRequest request) {
        Objects.requireNonNull(request, "Update request cannot be null");
        
        CourierProfile courierProfile = courierProfileRepository.findById(request.deliveryPersonId())
            .orElseThrow(() -> new DeliveryNotFoundException("Courier not found: " + request.deliveryPersonId()));
        
        courierProfile.updateVehicleInfo(request.vehicleInfo(), request.deliveryCapacity());
        
        return courierProfileRepository.save(courierProfile);
    }
    
    @Override
    public CourierProfile updateAvailability(UpdateAvailabilityRequest request) {
        Objects.requireNonNull(request, "Update request cannot be null");
        
        CourierProfile courierProfile = courierProfileRepository.findById(request.deliveryPersonId())
            .orElseThrow(() -> new DeliveryNotFoundException("Courier not found: " + request.deliveryPersonId()));
        
        courierProfile.updateAvailabilitySchedule(request.availabilitySchedule());
        
        return courierProfileRepository.save(courierProfile);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CourierProfile getCourierProfile(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        return courierProfileRepository.findById(deliveryPersonId)
            .orElseThrow(() -> new DeliveryNotFoundException("Courier not found: " + deliveryPersonId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourierProfile> getPendingRegistrations() {
        return courierProfileRepository.findByApprovalStatus(CourierApprovalStatus.PENDING);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourierProfile> getCouriersByStatus(CourierApprovalStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        
        return courierProfileRepository.findByApprovalStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourierProfile> getCouriersByCity(String city) {
        Objects.requireNonNull(city, "City cannot be null");
        
        return courierProfileRepository.findByCity(city);
    }
    
    @Override
    public CourierProfile suspendCourier(DeliveryPersonId deliveryPersonId, String reason) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        
        CourierProfile courierProfile = courierProfileRepository.findById(deliveryPersonId)
            .orElseThrow(() -> new DeliveryNotFoundException("Courier not found: " + deliveryPersonId));
        
        courierProfile.suspend(reason);
        
        return courierProfileRepository.save(courierProfile);
    }
    
    @Override
    public CourierProfile reactivateCourier(DeliveryPersonId deliveryPersonId) {
        Objects.requireNonNull(deliveryPersonId, "Delivery person ID cannot be null");
        
        CourierProfile courierProfile = courierProfileRepository.findById(deliveryPersonId)
            .orElseThrow(() -> new DeliveryNotFoundException("Courier not found: " + deliveryPersonId));
        
        courierProfile.approve("Reactivated", "Account reactivated by admin");
        
        return courierProfileRepository.save(courierProfile);
    }
}