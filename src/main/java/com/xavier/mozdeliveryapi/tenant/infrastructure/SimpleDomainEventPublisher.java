package com.xavier.mozdeliveryapi.tenant.infrastructure;

import com.xavier.mozdeliveryapi.shared.application.DomainEventPublisher;
import com.xavier.mozdeliveryapi.shared.domain.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple implementation of DomainEventPublisher for the tenant module.
 * This is a temporary implementation that logs events.
 * In a full implementation, this would integrate with Kafka or another event bus.
 */
@Component
public class SimpleDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleDomainEventPublisher.class);
    
    @Override
    public void publish(DomainEvent event) {
        logger.info("Publishing domain event: {} at {}", 
                   event.getClass().getSimpleName(), 
                   event.getOccurredOn());
        
        // TODO: Integrate with actual event bus (Kafka) in future iterations
        // For now, we just log the event
    }
}