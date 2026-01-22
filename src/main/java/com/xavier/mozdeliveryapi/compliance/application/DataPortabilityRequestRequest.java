package com.xavier.mozdeliveryapi.compliance.application;

/**
 * Request for data portability.
 */
public record DataPortabilityRequestRequest(
    String dataSubjectId,
    String tenantId,
    String format
) {
    public DataPortabilityRequestRequest {
        if (dataSubjectId == null || dataSubjectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Data subject ID cannot be null or empty");
        }
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or empty");
        }
        if (format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Format cannot be null or empty");
        }
        if (!format.trim().toUpperCase().matches("JSON|CSV|XML")) {
            throw new IllegalArgumentException("Format must be JSON, CSV, or XML");
        }
    }
}