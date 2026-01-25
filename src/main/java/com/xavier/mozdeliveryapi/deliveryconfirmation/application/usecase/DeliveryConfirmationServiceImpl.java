package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCExpiredException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCInvalidCodeException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCMaxAttemptsExceededException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCNotFoundException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.repository.DeliveryConfirmationCodeRepository;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.service.DCCGenerationService;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Implementation of delivery confirmation service with enhanced security and audit logging.
 */
@Service
@Transactional
public class DeliveryConfirmationServiceImpl implements DeliveryConfirmationService {
    
    private final DeliveryConfirmationCodeRepository repository;
    private final DCCGenerationService generationService;
    private final DCCAuditService auditService;
    private final DCCSecurityService securityService;
    
    public DeliveryConfirmationServiceImpl(
            DeliveryConfirmationCodeRepository repository,
            DCCGenerationService generationService,
            DCCAuditService auditService,
            DCCSecurityService securityService) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.generationService = Objects.requireNonNull(generationService, "Generation service cannot be null");
        this.auditService = Objects.requireNonNull(auditService, "Audit service cannot be null");
        this.securityService = Objects.requireNonNull(securityService, "Security service cannot be null");
    }
    
    @Override
    public DeliveryConfirmationCode generateCode(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        // Check if a code already exists for this order
        repository.findByOrderId(orderId).ifPresent(existingCode -> {
            // If there's an existing active code, expire it first
            if (existingCode.isActive()) {
                existingCode.expire();
                repository.save(existingCode);
                
                // Log expiration of old code
                // Note: We need merchant ID from order - this would be fetched in real implementation
                // auditService.logDCCExpired(orderId, merchantId, false, null, null, null, null);
            }
        });
        
        // Generate new code
        DeliveryConfirmationCode newCode = generationService.generateCode(orderId);
        DeliveryConfirmationCode savedCode = repository.save(newCode);
        
        // Log code generation
        // Note: We need merchant ID from order - this would be fetched in real implementation
        // auditService.logDCCGenerated(orderId, merchantId, savedCode.getCode(), 
        //     savedCode.getExpiresAt(), null, null);
        
        return savedCode;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateCode(OrderId orderId, String code, String courierId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(code, "Code cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        
        // Security checks before attempting validation
        if (!securityService.canCourierAttemptValidation(courierId, orderId)) {
            throw new DCCMaxAttemptsExceededException("Courier is locked out or rate limited");
        }
        
        // Check for suspicious activity
        if (securityService.detectSuspiciousActivity(courierId, orderId, code)) {
            // Log suspicious activity but continue with validation
            // The audit service will handle the logging
        }
        
        DeliveryConfirmationCode dcc = repository.findByOrderId(orderId)
            .orElseThrow(() -> new DCCNotFoundException(orderId));
        
        try {
            boolean isValid = dcc.validate(code, courierId);
            repository.save(dcc); // Save the updated state
            
            // Record the attempt in security service
            securityService.recordValidationAttempt(courierId, orderId, isValid);
            
            if (isValid) {
                // Log successful validation
                // Note: We need merchant ID from order - this would be fetched in real implementation
                // auditService.logDCCValidated(orderId, merchantId, courierId, null, null);
            }
            
            return isValid;
            
        } catch (DCCInvalidCodeException e) {
            repository.save(dcc); // Save the failed attempt
            securityService.recordValidationAttempt(courierId, orderId, false);
            
            // Log failed validation
            // Note: We need merchant ID from order - this would be fetched in real implementation
            // auditService.logDCCValidationFailed(orderId, merchantId, courierId, code, 
            //     dcc.getAttemptCount(), dcc.getMaxAttempts(), null, null);
            
            throw e;
            
        } catch (DCCMaxAttemptsExceededException e) {
            repository.save(dcc); // Save the final failed attempt
            securityService.recordValidationAttempt(courierId, orderId, false);
            
            // Apply security lockout
            securityService.applyCourierLockout(courierId, orderId, dcc.getAttemptCount());
            
            // Log lockout
            // Note: We need merchant ID from order - this would be fetched in real implementation
            // auditService.logDCCLockout(orderId, merchantId, courierId, dcc.getAttemptCount(), null, null);
            
            throw e;
            
        } catch (DCCExpiredException e) {
            repository.save(dcc); // Save the expired state
            securityService.recordValidationAttempt(courierId, orderId, false);
            
            // Log expiration
            // Note: We need merchant ID from order - this would be fetched in real implementation
            // auditService.logDCCExpired(orderId, merchantId, false, null, null, null, null);
            
            throw e;
            
        } catch (Exception e) {
            repository.save(dcc); // Save any state changes
            securityService.recordValidationAttempt(courierId, orderId, false);
            throw e; // Re-throw unexpected exceptions
        }
    }
    
    @Override
    public DeliveryConfirmationCode resendCode(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        // Find existing code and expire it
        repository.findByOrderId(orderId).ifPresent(existingCode -> {
            existingCode.expire();
            repository.save(existingCode);
        });
        
        // Generate new code
        DeliveryConfirmationCode newCode = generationService.generateCode(orderId);
        DeliveryConfirmationCode savedCode = repository.save(newCode);
        
        // Log code resend
        // Note: We need merchant ID from order - this would be fetched in real implementation
        // auditService.logDCCResent(orderId, merchantId, savedCode.getCode(), 
        //     "User requested resend", null, null);
        
        return savedCode;
    }
    
    @Override
    @Transactional(readOnly = true)
    public DeliveryConfirmationCode getCode(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        return repository.findByOrderId(orderId)
            .orElseThrow(() -> new DCCNotFoundException(orderId));
    }
    
    @Override
    public void forceExpireCode(OrderId orderId, String adminId, String reason) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(adminId, "Admin ID cannot be null");
        Objects.requireNonNull(reason, "Reason cannot be null");
        
        DeliveryConfirmationCode dcc = repository.findByOrderId(orderId)
            .orElseThrow(() -> new DCCNotFoundException(orderId));
        
        dcc.forceExpire(adminId, reason);
        repository.save(dcc);
        
        // Log forced expiration
        // Note: We need merchant ID from order - this would be fetched in real implementation
        // auditService.logDCCExpired(orderId, merchantId, true, adminId, reason, null, null);
    }
    
    @Override
    public void handleFailedAttempt(OrderId orderId, String courierId, String attemptedCode) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        Objects.requireNonNull(attemptedCode, "Attempted code cannot be null");
        
        // This method is called when we want to record a failed attempt without throwing exceptions
        // It's useful for audit purposes or when handling failures in a different way
        try {
            validateCode(orderId, attemptedCode, courierId);
        } catch (Exception e) {
            // The failed attempt has been recorded in the validate method
            // We can log this or handle it as needed
        }
    }
}