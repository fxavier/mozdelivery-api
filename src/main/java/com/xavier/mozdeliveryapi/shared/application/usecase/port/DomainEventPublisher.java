package com.xavier.mozdeliveryapi.shared.application.usecase.port;
import com.xavier.mozdeliveryapi.shared.domain.event.DomainEvent;


/**
 * Interface for publishing domain events.
 * This is a port in the hexagonal architecture.
 */
public interface DomainEventPublisher {
    
    /**
     * Publish a domain event.
     */
    void publish(DomainEvent event);
}