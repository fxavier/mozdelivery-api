package com.xavier.mozdeliveryapi.shared.application;

/**
 * Marker interface for use cases in the application layer.
 * Use cases represent application-specific business rules.
 */
public interface UseCase<REQUEST, RESPONSE> {
    
    /**
     * Execute the use case with the given request.
     */
    RESPONSE execute(REQUEST request);
}