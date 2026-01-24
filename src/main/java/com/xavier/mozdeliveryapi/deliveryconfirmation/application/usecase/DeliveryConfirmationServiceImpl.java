package com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCNotFoundException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.repository.DeliveryConfirmationCodeRepository;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.service.DCCGenerationService;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Implementation of delivery confirmation service.
 */
@Service
@Transactional
public class DeliveryConfirmationServiceImpl implements DeliveryConfirmationService {
    
    private final DeliveryConfirmationCodeRepository repository;
    private final DCCGenerationService generationService;
    
    public DeliveryConfirmationServiceImpl(
            DeliveryConfirmationCodeRepository repository,
            DCCGenerationService generationService) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.generationService = Objects.requireNonNull(generationService, "Generation service cannot be null");
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
            }
        });
        
        // Generate new code
        DeliveryConfirmationCode newCode = generationService.generateCode(orderId);
        return repository.save(newCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateCode(OrderId orderId, String code, String courierId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        Objects.requireNonNull(code, "Code cannot be null");
        Objects.requireNonNull(courierId, "Courier ID cannot be null");
        
        DeliveryConfirmationCode dcc = repository.findByOrderId(orderId)
            .orElseThrow(() -> new DCCNotFoundException(orderId));
        
        try {
            boolean isValid = dcc.validate(code, courierId);
            repository.save(dcc); // Save the updated state (attempt recorded, possibly status changed)
            return isValid;
        } catch (Exception e) {
            repository.save(dcc); // Save the failed attempt
            throw e; // Re-throw the exception
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
        return repository.save(newCode);
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