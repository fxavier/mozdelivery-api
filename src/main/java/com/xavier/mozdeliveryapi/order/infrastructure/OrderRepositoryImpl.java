package com.xavier.mozdeliveryapi.order.infrastructure;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.order.domain.CustomerId;
import com.xavier.mozdeliveryapi.order.domain.Order;
import com.xavier.mozdeliveryapi.order.domain.OrderId;
import com.xavier.mozdeliveryapi.order.domain.OrderRepository;
import com.xavier.mozdeliveryapi.order.domain.OrderStatus;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

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
    public List<Order> findByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        return jpaRepository.findByTenantId(tenantId.value())
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByTenantIdAndStatus(TenantId tenantId, OrderStatus status) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        return jpaRepository.findByTenantIdAndStatus(tenantId.value(), status)
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
    public List<Order> findByCustomerIdAndTenantId(CustomerId customerId, TenantId tenantId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        return jpaRepository.findByCustomerIdAndTenantId(customerId.value(), tenantId.value())
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
    public long countByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        return jpaRepository.countByTenantId(tenantId.value());
    }
    
    @Override
    public long countByTenantIdAndStatus(TenantId tenantId, OrderStatus status) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        
        return jpaRepository.countByTenantIdAndStatus(tenantId.value(), status);
    }
}
