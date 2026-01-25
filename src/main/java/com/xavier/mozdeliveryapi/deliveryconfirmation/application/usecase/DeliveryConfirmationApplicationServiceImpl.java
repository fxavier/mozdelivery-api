package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import java.time.Instant;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.CompleteDeliveryRequest;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.DCCStatusResponse;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.dto.DeliveryCompletionResult;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCExpiredException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCInvalidCodeException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCMaxAttemptsExceededException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCSecurityLockoutException;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Implementation of delivery confirmation application service with enhanced security.
 */
@Service
@Transactional
public class DeliveryConfirmationApplicationServiceImpl implements DeliveryConfirmationApplicationService {
    
    private final DeliveryConfirmationService deliveryConfirmationService;
    private final DCCSecurityService securityService;
    
    public DeliveryConfirmationApplicationServiceImpl(
            DeliveryConfirmationService deliveryConfirmationService,
            DCCSecurityService securityService) {
        this.deliveryConfirmationService = Objects.requireNonNull(deliveryConfirmationService, 
            "Delivery confirmation service cannot be null");
        this.securityService = Objects.requireNonNull(securityService, 
            "Security service cannot be null");
    }
    
    @Override
    public void generateDeliveryCode(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        deliveryConfirmationService.generateCode(orderId);
    }
    
    @Override
    public DeliveryCompletionResult completeDelivery(CompleteDeliveryRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
        // Pre-validation security checks
        if (securityService.isCourierLockedOut(request.courierId())) {
            long remainingLockout = securityService.getRemainingLockoutTime(request.courierId());
            return DeliveryCompletionResult.failure(
                request.orderId(), 
                String.format("Courier is locked out. Try again in %d seconds.", remainingLockout), 
                0
            );
        }
        
        if (securityService.isRateLimitExceeded(request.courierId())) {
            return DeliveryCompletionResult.failure(
                request.orderId(), 
                "Rate limit exceeded. Please wait before attempting again.", 
                0
            );
        }
        
        try {
            boolean isValid = deliveryConfirmationService.validateCode(
                request.orderId(), 
                request.confirmationCode(), 
                request.courierId()
            );
            
            if (isValid) {
                return DeliveryCompletionResult.success(request.orderId(), Instant.now());
            } else {
                // This shouldn't happen as validateCode throws exceptions for invalid codes
                return DeliveryCompletionResult.failure(
                    request.orderId(), 
                    "Validation failed", 
                    0
                );
            }
            
        } catch (DCCExpiredException e) {
            return DeliveryCompletionResult.failure(
                request.orderId(), 
                "Delivery confirmation code has expired. Please request a new code.", 
                0
            );
            
        } catch (DCCMaxAttemptsExceededException e) {
            // Check if courier is now locked out
            long lockoutTime = securityService.getRemainingLockoutTime(request.courierId());
            String message = lockoutTime > 0 
                ? String.format("Maximum validation attempts exceeded. Courier locked out for %d seconds.", lockoutTime)
                : "Maximum validation attempts exceeded.";
                
            return DeliveryCompletionResult.failure(
                request.orderId(), 
                message, 
                0
            );
            
        } catch (DCCInvalidCodeException e) {
            // Get remaining attempts
            try {
                DeliveryConfirmationCode dcc = deliveryConfirmationService.getCode(request.orderId());
                int remainingAttempts = dcc.getRemainingAttempts();
                
                String message = remainingAttempts > 0 
                    ? String.format("Invalid confirmation code. %d attempts remaining.", remainingAttempts)
                    : "Invalid confirmation code. No attempts remaining.";
                
                return DeliveryCompletionResult.failure(
                    request.orderId(), 
                    message, 
                    remainingAttempts
                );
            } catch (Exception ex) {
                return DeliveryCompletionResult.failure(
                    request.orderId(), 
                    "Invalid confirmation code.", 
                    0
                );
            }
            
        } catch (DCCSecurityLockoutException e) {
            return DeliveryCompletionResult.failure(
                request.orderId(), 
                String.format("Security lockout active. Try again in %d seconds.", e.getLockoutRemainingSeconds()), 
                0
            );
            
        } catch (Exception e) {
            return DeliveryCompletionResult.failure(
                request.orderId(), 
                "Delivery completion failed: " + e.getMessage(), 
                0
            );
        }
    }
    
    @Override
    public void resendDeliveryCode(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        deliveryConfirmationService.resendCode(orderId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DCCStatusResponse getCodeStatus(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        DeliveryConfirmationCode dcc = deliveryConfirmationService.getCode(orderId);
        
        return new DCCStatusResponse(
            dcc.getOrderId(),
            dcc.getStatus(),
            dcc.isActive(),
            dcc.isExpired(),
            dcc.getGeneratedAt(),
            dcc.getExpiresAt(),
            dcc.getAttemptCount(),
            dcc.getMaxAttempts(),
            dcc.getRemainingAttempts()
        );
    }
    
    /**
     * Get courier validation statistics for security monitoring.
     */
    @Transactional(readOnly = true)
    public DCCSecurityService.ValidationStats getCourierValidationStats(String courierId, Instant since) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(since, "Since timestamp cannot be null");
        
        return securityService.getCourierValidationStats(courierId, since);
    }
    
    /**
     * Clear courier lockout (admin function).
     */
    public void clearCourierLockout(String courierId, String adminId, String reason) {
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(adminId, "Admin ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        
        securityService.clearCourierLockout(courierId, adminId, reason);
    }
}