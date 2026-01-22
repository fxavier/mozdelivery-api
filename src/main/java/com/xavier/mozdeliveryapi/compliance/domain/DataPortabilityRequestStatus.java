package com.xavier.mozdeliveryapi.compliance.domain;

/**
 * Status of a data portability request.
 */
public enum DataPortabilityRequestStatus {
    /**
     * Request has been submitted and is pending processing.
     */
    PENDING,
    
    /**
     * Request is being processed.
     */
    PROCESSING,
    
    /**
     * Request has been completed and data is ready for download.
     */
    COMPLETED,
    
    /**
     * Request has failed due to an error.
     */
    FAILED,
    
    /**
     * Request has been cancelled by the data subject.
     */
    CANCELLED,
    
    /**
     * Request has expired (data download link expired).
     */
    EXPIRED;
    
    /**
     * Check if the request is in a final state.
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == EXPIRED;
    }
    
    /**
     * Check if the request can be cancelled.
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == PROCESSING;
    }
}