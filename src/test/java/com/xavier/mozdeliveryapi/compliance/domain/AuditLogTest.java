package com.xavier.mozdeliveryapi.compliance.domain;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.compliance.domain.entity.AuditLog;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditEventType;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditLogId;
import com.xavier.mozdeliveryapi.compliance.domain.valueobject.AuditSeverity;

/**
 * Unit tests for AuditLog aggregate.
 */
class AuditLogTest {
    
    @Test
    void shouldCreateValidAuditLog() {
        // Given
        AuditLogId auditLogId = AuditLogId.generate();
        TenantId tenantId = TenantId.generate();
        String userId = "user123";
        AuditEventType eventType = AuditEventType.ORDER_CREATED;
        AuditSeverity severity = AuditSeverity.INFO;
        String resource = "orders";
        String action = "CREATE";
        String description = "Order created successfully";
        Map<String, Object> metadata = Map.of("orderId", "order123");
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        String sessionId = "session123";
        
        // When
        AuditLog auditLog = new AuditLog(auditLogId, tenantId, userId, eventType, severity,
                                        resource, action, description, metadata, ipAddress, userAgent, sessionId);
        
        // Then
        assertThat(auditLog.getAuditLogId()).isEqualTo(auditLogId);
        assertThat(auditLog.getTenantId()).isEqualTo(tenantId);
        assertThat(auditLog.getUserId()).isEqualTo(userId);
        assertThat(auditLog.getEventType()).isEqualTo(eventType);
        assertThat(auditLog.getSeverity()).isEqualTo(severity);
        assertThat(auditLog.getResource()).isEqualTo(resource);
        assertThat(auditLog.getAction()).isEqualTo(action);
        assertThat(auditLog.getDescription()).isEqualTo(description);
        assertThat(auditLog.getMetadata()).isEqualTo(metadata);
        assertThat(auditLog.getIpAddress()).isEqualTo(ipAddress);
        assertThat(auditLog.getUserAgent()).isEqualTo(userAgent);
        assertThat(auditLog.getSessionId()).isEqualTo(sessionId);
        assertThat(auditLog.getTimestamp()).isNotNull();
        assertThat(auditLog.getChecksum()).isNotNull();
        assertThat(auditLog.verifyIntegrity()).isTrue();
    }
    
    @Test
    void shouldIdentifySecurityEvents() {
        // Given
        AuditLog securityLog = createAuditLog(AuditEventType.SUSPICIOUS_ACTIVITY, AuditSeverity.WARNING);
        AuditLog normalLog = createAuditLog(AuditEventType.ORDER_CREATED, AuditSeverity.INFO);
        
        // Then
        assertThat(securityLog.isSecurityEvent()).isTrue();
        assertThat(normalLog.isSecurityEvent()).isFalse();
    }
    
    @Test
    void shouldIdentifyComplianceEvents() {
        // Given
        AuditLog complianceLog = createAuditLog(AuditEventType.CONSENT_GIVEN, AuditSeverity.INFO);
        AuditLog normalLog = createAuditLog(AuditEventType.ORDER_CREATED, AuditSeverity.INFO);
        
        // Then
        assertThat(complianceLog.isComplianceEvent()).isTrue();
        assertThat(normalLog.isComplianceEvent()).isFalse();
    }
    
    @Test
    void shouldRejectNullEventType() {
        assertThatThrownBy(() -> createAuditLogBuilder().eventType(null).build())
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Event type cannot be null");
    }
    
    @Test
    void shouldRejectEmptyResource() {
        assertThatThrownBy(() -> new AuditLog(AuditLogId.generate(), TenantId.generate(), "user123",
                                             AuditEventType.ORDER_CREATED, AuditSeverity.INFO, "", "CREATE", "Test description",
                                             Map.of(), "127.0.0.1", "Mozilla/5.0", "session123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Resource cannot be empty");
    }
    
    private AuditLog createAuditLog(AuditEventType eventType, AuditSeverity severity) {
        return createAuditLogBuilder()
            .eventType(eventType)
            .severity(severity)
            .build();
    }
    
    private AuditLogBuilder createAuditLogBuilder() {
        return new AuditLogBuilder();
    }
    
    private static class AuditLogBuilder {
        private AuditEventType eventType = AuditEventType.ORDER_CREATED;
        private AuditSeverity severity = AuditSeverity.INFO;
        
        public AuditLogBuilder eventType(AuditEventType eventType) {
            this.eventType = eventType;
            return this;
        }
        
        public AuditLogBuilder severity(AuditSeverity severity) {
            this.severity = severity;
            return this;
        }
        
        public AuditLogBuilder resource(String resource) {
            // This would be used in the actual build method
            return this;
        }
        
        public AuditLog build() {
            return new AuditLog(AuditLogId.generate(), TenantId.generate(), "user123",
                               eventType, severity, "orders", "CREATE", "Test description",
                               Map.of(), "127.0.0.1", "Mozilla/5.0", "session123");
        }
    }
}
