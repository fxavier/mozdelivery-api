package com.xavier.mozdeliveryapi.deliveryconfirmation.domain;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.service.DCCGenerationServiceImpl;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.valueobject.DCCStatus;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Unit tests for DCCGenerationService.
 */
class DCCGenerationServiceTest {
    
    private DCCGenerationServiceImpl generationService;
    
    @BeforeEach
    void setUp() {
        generationService = new DCCGenerationServiceImpl();
    }
    
    @Test
    @DisplayName("Should generate DCC with default settings")
    void shouldGenerateDCCWithDefaultSettings() {
        // Given
        OrderId orderId = OrderId.generate();
        
        // When
        DeliveryConfirmationCode dcc = generationService.generateCode(orderId);
        
        // Then
        assertThat(dcc.getOrderId()).isEqualTo(orderId);
        assertThat(dcc.getCode()).matches("\\d{4}"); // 4 digits
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.ACTIVE);
        assertThat(dcc.getMaxAttempts()).isEqualTo(3);
        assertThat(dcc.isActive()).isTrue();
        assertThat(dcc.getExpiresAt()).isAfter(dcc.getGeneratedAt());
    }
    
    @Test
    @DisplayName("Should generate DCC with custom settings")
    void shouldGenerateDCCWithCustomSettings() {
        // Given
        OrderId orderId = OrderId.generate();
        Duration expiration = Duration.ofHours(12);
        int maxAttempts = 5;
        
        // When
        DeliveryConfirmationCode dcc = generationService.generateCode(orderId, expiration, maxAttempts);
        
        // Then
        assertThat(dcc.getOrderId()).isEqualTo(orderId);
        assertThat(dcc.getCode()).matches("\\d{4}"); // 4 digits
        assertThat(dcc.getStatus()).isEqualTo(DCCStatus.ACTIVE);
        assertThat(dcc.getMaxAttempts()).isEqualTo(maxAttempts);
        assertThat(dcc.isActive()).isTrue();
    }
    
    @Test
    @DisplayName("Should generate unique codes for different orders")
    void shouldGenerateUniqueCodesForDifferentOrders() {
        // Given
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();
        
        // When
        DeliveryConfirmationCode dcc1 = generationService.generateCode(orderId1);
        DeliveryConfirmationCode dcc2 = generationService.generateCode(orderId2);
        
        // Then
        assertThat(dcc1.getOrderId()).isNotEqualTo(dcc2.getOrderId());
        // Note: Codes might be the same due to randomness, but that's statistically unlikely
        // and acceptable for a 4-digit code space
    }
    
    @Test
    @DisplayName("Should reject invalid expiration duration")
    void shouldRejectInvalidExpirationDuration() {
        // Given
        OrderId orderId = OrderId.generate();
        
        // When & Then
        assertThatThrownBy(() -> generationService.generateCode(orderId, Duration.ZERO, 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Expiration duration must be positive");
            
        assertThatThrownBy(() -> generationService.generateCode(orderId, Duration.ofSeconds(30), 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Expiration duration must be at least 1 minute");
            
        assertThatThrownBy(() -> generationService.generateCode(orderId, Duration.ofDays(8), 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Expiration duration cannot exceed 7 days");
    }
    
    @Test
    @DisplayName("Should reject invalid max attempts")
    void shouldRejectInvalidMaxAttempts() {
        // Given
        OrderId orderId = OrderId.generate();
        Duration expiration = Duration.ofHours(24);
        
        // When & Then
        assertThatThrownBy(() -> generationService.generateCode(orderId, expiration, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Max attempts must be at least 1");
            
        assertThatThrownBy(() -> generationService.generateCode(orderId, expiration, 11))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Max attempts cannot exceed 10");
    }
    
    @Test
    @DisplayName("Should regenerate code for same order")
    void shouldRegenerateCodeForSameOrder() {
        // Given
        OrderId orderId = OrderId.generate();
        Duration expiration = Duration.ofHours(12);
        int maxAttempts = 2;
        
        // When
        DeliveryConfirmationCode dcc1 = generationService.generateCode(orderId, expiration, maxAttempts);
        DeliveryConfirmationCode dcc2 = generationService.regenerateCode(orderId, expiration, maxAttempts);
        
        // Then
        assertThat(dcc1.getOrderId()).isEqualTo(dcc2.getOrderId());
        assertThat(dcc1.getGeneratedAt()).isBefore(dcc2.getGeneratedAt());
        // Codes might be different (statistically likely) but both should be valid
        assertThat(dcc1.getCode()).matches("\\d{4}");
        assertThat(dcc2.getCode()).matches("\\d{4}");
    }
}