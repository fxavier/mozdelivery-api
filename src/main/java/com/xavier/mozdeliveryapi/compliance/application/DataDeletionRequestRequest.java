package com.xavier.mozdeliveryapi.compliance.application;

/**
 * Request for data deletion.
 */
public record DataDeletionRequestRequest(
    String dataSubjectId,
    String tenantId,
    String reason
) {
    public DataDeletionRequestRequest {
        if (dataSubjectId == null || dataSubjectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Data subject ID cannot be null or empty");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or empty");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be null or empty");
        }
    }
}