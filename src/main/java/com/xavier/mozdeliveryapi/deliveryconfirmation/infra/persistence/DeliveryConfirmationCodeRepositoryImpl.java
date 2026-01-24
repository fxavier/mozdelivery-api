package com.xavier.mozdeliveryapi.deliveryconfirmation.infra.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.repository.DeliveryConfirmationCodeRepository;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject.DCCStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * In-memory implementation of DeliveryConfirmationCodeRepository.
 * This is a temporary implementation for development and testing.
 * In production, this should be replaced with a proper database implementation.
 */
@Repository
public class DeliveryConfirmationCodeRepositoryImpl implements DeliveryConfirmationCodeRepository {
    
    private final ConcurrentHashMap<OrderId, DeliveryConfirmationCode> storage = new ConcurrentHashMap<>();
    
    @Override
    public DeliveryConfirmationCode save(DeliveryConfirmationCode dcc) {
        storage.put(dcc.getOrderId(), dcc);
        return dcc;
    }
    
    @Override
    public Optional<DeliveryConfirmationCode> findByOrderId(OrderId orderId) {
        return Optional.ofNullable(storage.get(orderId));
    }
    
    @Override
    public List<DeliveryConfirmationCode> findByStatus(DCCStatus status) {
        return storage.values().stream()
            .filter(dcc -> dcc.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeliveryConfirmationCode> findExpiredCodes(Instant currentTime) {
        return storage.values().stream()
            .filter(dcc -> dcc.getExpiresAt().isBefore(currentTime))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeliveryConfirmationCode> findByGeneratedAtBetween(Instant start, Instant end) {
        return storage.values().stream()
            .filter(dcc -> !dcc.getGeneratedAt().isBefore(start) && !dcc.getGeneratedAt().isAfter(end))
            .collect(Collectors.toList());
    }
    
    @Override
    public void delete(DeliveryConfirmationCode dcc) {
        storage.remove(dcc.getOrderId());
    }
    
    @Override
    public void deleteByOrderId(OrderId orderId) {
        storage.remove(orderId);
    }
    
    @Override
    public boolean existsByOrderId(OrderId orderId) {
        return storage.containsKey(orderId);
    }
}