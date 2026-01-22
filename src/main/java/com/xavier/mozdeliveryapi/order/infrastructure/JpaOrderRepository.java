package com.xavier.mozdeliveryapi.order.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xavier.mozdeliveryapi.order.domain.OrderStatus;

/**
 * Spring Data JPA repository for OrderEntity.
 */
@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    
    List<OrderEntity> findByTenantId(UUID tenantId);
    
    List<OrderEntity> findByTenantIdAndStatus(UUID tenantId, OrderStatus status);
    
    List<OrderEntity> findByCustomerId(UUID customerId);
    
    List<OrderEntity> findByCustomerIdAndTenantId(UUID customerId, UUID tenantId);
    
    List<OrderEntity> findByStatus(OrderStatus status);
    
    long countByTenantId(UUID tenantId);
    
    long countByTenantIdAndStatus(UUID tenantId, OrderStatus status);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.tenantId = :tenantId ORDER BY o.createdAt DESC")
    List<OrderEntity> findByTenantIdOrderByCreatedAtDesc(@Param("tenantId") UUID tenantId);
}