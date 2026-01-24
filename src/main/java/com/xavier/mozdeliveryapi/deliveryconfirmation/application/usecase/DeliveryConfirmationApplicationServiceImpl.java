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
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Implementation of delivery confirmation application service.
 */
@Service
@Transactional
public class DeliveryConfirmationApplicationServiceImpl implements DeliveryConfirmationApplicationService {
    
    private final DeliveryConfirmationService deliveryConfirmationService;
    
    public DeliveryConfirmationApplicationServiceImpl(DeliveryConfirmationService deliveryConfirmationService) {
        this.deliveryConfirmationService = Objects.requireNonNull(deliveryConfirmationService, 
            "Delivery confirmation service cannot be null");
    }
    
    @Override
    public void generateDeliveryCode(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        deliveryConfirmationService.generateCode(orderId);
    }
    
    @Override
    public DeliveryCompletionResult completeDelivery(CompleteDeliveryRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        
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
                "Delivery confirmation code has expired", 
                0
            );
            
        } catch (DCCMaxAttemptsExceededException e) {
            return DeliveryCompletionResult.failure(
                request.orderId(), 
                "Maximum validation attempts exceeded", 
                0
            );
            
        } catch (DCCInvalidCodeException e) {
            // Get remaining attempts
            DeliveryConfirmationCode dcc = deliveryConfirmationService.getCode(request.orderId());
            int remainingAttempts = dcc.getRemainingAttempts();
            
            return DeliveryCompletionResult.failure(
                request.orderId(), 
                "Invalid confirmation code. " + remainingAttempts + " attempts remaining.", 
                remainingAttempts
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
}