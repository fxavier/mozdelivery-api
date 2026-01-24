package com.xavier.mozdeliveryapi.deliveryconfirmation.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject.DCCStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Repository interface for Delivery Confirmation Code aggregate.
 */
public interface DeliveryConfirmationCodeRepository {
    
    /**
     * Save a delivery confirmation code.
     */
    DeliveryConfirmationCode save(DeliveryConfirmationCode dcc);
    
    /**
     * Find a delivery confirmation code by order ID.
     */
    Optional<DeliveryConfirmationCode> findByOrderId(OrderId orderId);
    
    /**
     * Find all delivery confirmation codes with the specified status.
     */
    List<DeliveryConfirmationCode> findByStatus(DCCStatus status);
    
    /**
     * Find all delivery confirmation codes that have expired.
     */
    List<DeliveryConfirmationCode> findExpiredCodes(Instant currentTime);
    
    /**
     * Find all delivery confirmation codes generated within a time range.
     */
    List<DeliveryConfirmationCode> findByGeneratedAtBetween(Instant start, Instant end);
    
    /**
     * Delete a delivery confirmation code.
     */
    void delete(DeliveryConfirmationCode dcc);
    
    /**
     * Delete delivery confirmation code by order ID.
     */
    void deleteByOrderId(OrderId orderId);
    
    /**
     * Check if a delivery confirmation code exists for the given order.
     */
    boolean existsByOrderId(OrderId orderId);
}