package com.xavier.mozdeliveryapi.order.infra.persistence;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.order.application.usecase.port.OrderRepository;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestTrackingToken;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Implementation of OrderRepository using JPA.
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    private final JpaOrderRepository jpaRepository;
    private final OrderMapper mapper;
    
    public OrderRepositoryImpl(JpaOrderRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "JPA repository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "Mapper cannot be null");
    }
    
    @Override
    public Order save(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Order> findById(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        return jpaRepository.findById(orderId.value())
            .map(mapper::toDomain);
    }
    
    @Override
    public List<Order> findByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.findByTenantId(merchantId.value())
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByMerchantIdAndStatus(MerchantId merchantId, OrderStatus status) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        return jpaRepository.findByTenantIdAndStatus(merchantId.value(), status)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        
        return jpaRepository.findByCustomerId(customerId.value())
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByCustomerIdAndMerchantId(CustomerId customerId, MerchantId merchantId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.findByCustomerIdAndTenantId(customerId.value(), merchantId.value())
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        
        return jpaRepository.findByStatus(status)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsById(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        return jpaRepository.existsById(orderId.value());
    }
    
    @Override
    public void deleteById(OrderId orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        
        jpaRepository.deleteById(orderId.value());
    }
    
    @Override
    public void delete(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        jpaRepository.deleteById(order.getOrderId().value());
    }
    
    @Override
    public long countByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.countByTenantId(merchantId.value());
    }
    
    @Override
    public long countByMerchantIdAndStatus(MerchantId merchantId, OrderStatus status) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        return jpaRepository.countByTenantIdAndStatus(merchantId.value(), status);
    }
    
    @Override
    public Optional<Order> findByGuestTrackingToken(GuestTrackingToken token) {
        Objects.requireNonNull(token, "Guest tracking token cannot be null");
        
        return jpaRepository.findByGuestTrackingToken(token.getValue())
            .map(mapper::toDomain);
    }
    
    @Override
    public List<Order> findGuestOrdersByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.findByTenantIdAndGuestInfoIsNotNull(merchantId.value())
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findGuestOrdersByMerchantIdAndStatus(MerchantId merchantId, OrderStatus status) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        return jpaRepository.findByTenantIdAndStatusAndGuestInfoIsNotNull(merchantId.value(), status)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public long countGuestOrdersByMerchantId(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return jpaRepository.countByTenantIdAndGuestInfoIsNotNull(merchantId.value());
    }
}
