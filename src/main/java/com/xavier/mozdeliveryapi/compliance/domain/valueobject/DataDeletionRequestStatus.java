package com.xavier.mozdeliveryapi.compliance.domain.valueobject;

/**
 * Status of a data deletion request.
 */
public enum DataDeletionRequestStatus {
    /**
     * Request has been submitted and is pending processing.
     */
    PENDING,
    
    /**
     * Request is being processed.
     */
    PROCESSING,
    
    /**
     * Request has been completed and data has been deleted.
     */
    COMPLETED,
    
    /**
     * Request has failed due to an error.
     */
    FAILED,
    
    /**
     * Request has been cancelled by the data subject.
     */
    CANCELLED;
    
    /**
     * Check if the request is in a final state.
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    /**
     * Check if the request can be cancelled.
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == PROCESSING;
    }
}