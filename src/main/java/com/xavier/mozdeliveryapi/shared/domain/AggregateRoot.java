package com.xavier.mozdeliveryapi.shared.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for aggregate roots in the domain model.
 * Provides event publishing capabilities and common aggregate behavior.
 */
public abstract class AggregateRoot<ID extends ValueObject> {
    
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected abstract ID getId();
    
    /**
     * Register a domain event to be published after the aggregate is persisted.
     */
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * Get all domain events registered by this aggregate.
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * Clear all domain events. Should be called after events are published.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        AggregateRoot<?> that = (AggregateRoot<?>) obj;
        return getId() != null && getId().equals(that.getId());
    }
    
    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}