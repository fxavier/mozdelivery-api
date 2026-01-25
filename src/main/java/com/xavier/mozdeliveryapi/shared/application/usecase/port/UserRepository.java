package com.xavier.mozdeliveryapi.shared.application.usecase.port;

import com.xavier.mozdeliveryapi.shared.domain.entity.User;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User aggregate.
 */
public interface UserRepository extends Repository<User, UserId> {
    
    /**
     * Save a user.
     */
    User save(User user);
    
    /**
     * Find user by ID.
     */
    Optional<User> findById(UserId userId);
    
    /**
     * Find user by email.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role.
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find users by merchant ID.
     */
    List<User> findByMerchantId(MerchantId merchantId);
    
    /**
     * Find users by role and merchant ID.
     */
    List<User> findByRoleAndMerchantId(UserRole role, MerchantId merchantId);
    
    /**
     * Find active users.
     */
    List<User> findByActive(boolean active);
    
    /**
     * Count users by role.
     */
    long countByRole(UserRole role);
    
    /**
     * Count users by merchant ID.
     */
    long countByMerchantId(MerchantId merchantId);
    
    /**
     * Delete a user (for testing purposes).
     */
    void delete(UserId userId);
}