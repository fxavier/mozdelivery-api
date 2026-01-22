package com.xavier.mozdeliveryapi.compliance.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

/**
 * Unit tests for Consent aggregate.
 */
class ConsentTest {
    
    @Test
    void shouldCreateValidConsent() {
        // Given
        ConsentId consentId = ConsentId.generate();
        DataSubjectId dataSubjectId = DataSubjectId.generate();
        TenantId tenantId = TenantId.generate();
        ConsentType consentType = ConsentType.ORDER_PROCESSING;
        String purpose = "Processing personal data for order fulfillment";
        String ipAddress = "192.168.1.1";
        String userAgent = "Mozilla/5.0";
        
        // When
        Consent consent = new Consent(consentId, dataSubjectId, tenantId, consentType, 
                                     purpose, ipAddress, userAgent);
        
        // Then
        assertThat(consent.getConsentId()).isEqualTo(consentId);
        assertThat(consent.getDataSubjectId()).isEqualTo(dataSubjectId);
        assertThat(consent.getTenantId()).isEqualTo(tenantId);
        assertThat(consent.getConsentType()).isEqualTo(consentType);
        assertThat(consent.getStatus()).isEqualTo(ConsentStatus.GIVEN);
        assertThat(consent.getPurpose()).isEqualTo(purpose);
        assertThat(consent.isValid()).isTrue();
        assertThat(consent.allowsProcessing()).isTrue();
    }
    
    @Test
    void shouldWithdrawConsent() {
        // Given
        Consent consent = createValidConsent();
        
        // When
        consent.withdraw();
        
        // Then
        assertThat(consent.getStatus()).isEqualTo(ConsentStatus.WITHDRAWN);
        assertThat(consent.isValid()).isFalse();
        assertThat(consent.allowsProcessing()).isFalse();
        assertThat(consent.getWithdrawnAt()).isNotNull();
    }
    
    @Test
    void shouldRejectNullConsentId() {
        assertThatThrownBy(() -> new Consent(null, DataSubjectId.generate(), TenantId.generate(),
                                           ConsentType.ORDER_PROCESSING, "Purpose", "127.0.0.1", "Agent"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Consent ID cannot be null");
    }
    
    @Test
    void shouldRejectEmptyPurpose() {
        assertThatThrownBy(() -> new Consent(ConsentId.generate(), DataSubjectId.generate(), TenantId.generate(),
                                           ConsentType.ORDER_PROCESSING, "", "127.0.0.1", "Agent"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Purpose cannot be empty");
    }
    
    private Consent createValidConsent() {
        return new Consent(ConsentId.generate(), DataSubjectId.generate(), TenantId.generate(),
                          ConsentType.ORDER_PROCESSING, "Processing personal data for order fulfillment",
                          "192.168.1.1", "Mozilla/5.0");
    }
}