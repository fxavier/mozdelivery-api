package com.xavier.mozdeliveryapi.deliveryconfirmation.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCExpiredException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCInvalidCodeException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCMaxAttemptsExceededException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject.DCCStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Unit tests for DeliveryConfirmationCode aggregate.
 */
class DeliveryConfirmationCodeTest {
    
    @Test
    @DisplayName("Should create DCC with valid parameters")
    void shouldCreateDCCWithValidParameters() {
        // Given
        OrderId orderId = OrderId.generate();
        String code = "1234";
        Instant expiresAt = Instant.now().plus(Duration.ofHours(24));
        int maxAttempts = 3;
        
        // When
        DeliveryConfirmationCode dcc = new DeliveryConfirmationCode(orderId, code, expiresAt, maxAttempts);
        
        // Then
        assertThat(dcc.getOrderId()).isEqualTo(orderId);
        assertThat(dcc.getCode()).isEqualTo(code);
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.ACTIVE);
        assertThat(dcc.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(dcc.getMaxAttempts()).isEqualTo(maxAttempts);
        assertThat(dcc.getAttemptCount()).isZero();
        assertThat(dcc.isActive()).isTrue();
        assertThat(dcc.isExpired()).isFalse();
        assertThat(dcc.isUsed()).isFalse();
        assertThat(dcc.getRemainingAttempts()).isEqualTo(maxAttempts);
    }
    
    @Test
    @DisplayName("Should reject invalid code format")
    void shouldRejectInvalidCodeFormat() {
        // Given
        OrderId orderId = OrderId.generate();
        Instant expiresAt = Instant.now().plus(Duration.ofHours(24));
        
        // When & Then
        assertThatThrownBy(() -> new DeliveryConfirmationCode(orderId, "123", expiresAt, 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("DCC must be exactly 4 digits");
            
        assertThatThrownBy(() -> new DeliveryConfirmationCode(orderId, "12345", expiresAt, 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("DCC must be exactly 4 digits");
            
        assertThatThrownBy(() -> new DeliveryConfirmationCode(orderId, "12ab", expiresAt, 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("DCC must contain only digits");
    }
    
    @Test
    @DisplayName("Should validate correct code successfully")
    void shouldValidateCorrectCodeSuccessfully() {
        // Given
        OrderId orderId = OrderId.generate();
        String code = "1234";
        Instant expiresAt = Instant.now().plus(Duration.ofHours(24));
        DeliveryConfirmationCode dcc = new DeliveryConfirmationCode(orderId, code, expiresAt, 3);
        String courierId = "courier-456";
        
        // When
        boolean result = dcc.validate(code, courierId);
        
        // Then
        assertThat(result).isTrue();
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.USED);
        assertThat(dcc.getAttemptCount()).isEqualTo(1);
        assertThat(dcc.isUsed()).isTrue();
        assertThat(dcc.isActive()).isFalse();
    }
    
    @Test
    @DisplayName("Should throw exception for incorrect code")
    void shouldThrowExceptionForIncorrectCode() {
        // Given
        OrderId orderId = OrderId.generate();
        String code = "1234";
        Instant expiresAt = Instant.now().plus(Duration.ofHours(24));
        DeliveryConfirmationCode dcc = new DeliveryConfirmationCode(orderId, code, expiresAt, 3);
        String courierId = "courier-456";
        
        // When & Then
        assertThatThrownBy(() -> dcc.validate("5678", courierId))
            .isInstanceOf(DCCInvalidCodeException.class)
            .hasMessage("Invalid confirmation code");
            
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.ACTIVE);
        assertThat(dcc.getAttemptCount()).isEqualTo(1);
        assertThat(dcc.getRemainingAttempts()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should expire after max attempts exceeded")
    void shouldExpireAfterMaxAttemptsExceeded() {
        // Given
        OrderId orderId = OrderId.generate();
        String code = "1234";
        Instant expiresAt = Instant.now().plus(Duration.ofHours(24));
        DeliveryConfirmationCode dcc = new DeliveryConfirmationCode(orderId, code, expiresAt, 2);
        String courierId = "courier-456";
        
        // When - First failed attempt
        assertThatThrownBy(() -> dcc.validate("5678", courierId))
            .isInstanceOf(DCCInvalidCodeException.class);
        
        // Then - Should still be active
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.ACTIVE);
        assertThat(dcc.getAttemptCount()).isEqualTo(1);
        
        // When - Second failed attempt (max reached)
        assertThatThrownBy(() -> dcc.validate("9999", courierId))
            .isInstanceOf(DCCMaxAttemptsExceededException.class)
            .hasMessage("Maximum validation attempts exceeded");
        
        // Then - Should be expired
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.EXPIRED);
        assertThat(dcc.getAttemptCount()).isEqualTo(2);
        assertThat(dcc.getRemainingAttempts()).isZero();
    }
    
    @Test
    @DisplayName("Should throw exception for expired code")
    void shouldThrowExceptionForExpiredCode() {
        // Given
        OrderId orderId = OrderId.generate();
        String code = "1234";
        Instant generatedAt = Instant.now().minus(Duration.ofHours(2));
        Instant expiresAt = Instant.now().minus(Duration.ofHours(1)); // Already expired
        // Use reconstitution constructor to create an expired DCC
        DeliveryConfirmationCode dcc = new DeliveryConfirmationCode(
            orderId, code, DCCStatus.ACTIVE, generatedAt, expiresAt, 3, 0, new ArrayList<>()
        );
        String courierId = "courier-456";
        
        // When & Then
        assertThatThrownBy(() -> dcc.validate(code, courierId))
            .isInstanceOf(DCCExpiredException.class)
            .hasMessage("DCC has expired");
            
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.EXPIRED);
        assertThat(dcc.isExpired()).isTrue();
    }
    
    @Test
    @DisplayName("Should force expire with admin action")
    void shouldForceExpireWithAdminAction() {
        // Given
        OrderId orderId = OrderId.generate();
        String code = "1234";
        Instant expiresAt = Instant.now().plus(Duration.ofHours(24));
        DeliveryConfirmationCode dcc = new DeliveryConfirmationCode(orderId, code, expiresAt, 3);
        String adminId = "admin-789";
        String reason = "Customer requested cancellation";
        
        // When
        dcc.forceExpire(adminId, reason);
        
        // Then
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.EXPIRED);
        assertThat(dcc.isActive()).isFalse();
    }
    
    @Test
    @DisplayName("Should reject invalid max attempts")
    void shouldRejectInvalidMaxAttempts() {
        // Given
        OrderId orderId = OrderId.generate();
        String code = "1234";
        Instant expiresAt = Instant.now().plus(Duration.ofHours(24));
        
        // When & Then
        assertThatThrownBy(() -> new DeliveryConfirmationCode(orderId, code, expiresAt, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Max attempts must be at least 1");
            
        assertThatThrownBy(() -> new DeliveryConfirmationCode(orderId, code, expiresAt, 11))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Max attempts cannot exceed 10");
    }
}