package com.xavier.mozdeliveryapi.shared.domain;

import java.time.Instant;

/**
 * Base interface for domain events.
 * Domain events represent something important that happened in the domain.
 */
public interface DomainEvent {
    
    /**
     * Get the timestamp when this event occurred.
     */
    Instant getOccurredOn();
    
    /**
     * Get the aggregate ID that this event relates to.
     */
    String getAggregateId();
    
    /**
     * Get the event type identifier.
     */
    String getEventType();
}