package com.xavier.mozdeliveryapi.shared.application.usecase.port;

import java.util.Optional;
import com.xavier.mozdeliveryapi.shared.domain.entity.AggregateRoot;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Base interface for repositories in the hexagonal architecture.
 * Repositories provide access to aggregate roots.
 */
public interface Repository<T extends AggregateRoot<ID>, ID extends ValueObject> {
    
    /**
     * Save an aggregate root.
     */
    T save(T aggregate);
    
    /**
     * Find an aggregate root by its ID.
     */
    Optional<T> findById(ID id);
    
    /**
     * Delete an aggregate root.
     */
    void delete(T aggregate);
    
    /**
     * Check if an aggregate root exists by its ID.
     */
    boolean existsById(ID id);
}