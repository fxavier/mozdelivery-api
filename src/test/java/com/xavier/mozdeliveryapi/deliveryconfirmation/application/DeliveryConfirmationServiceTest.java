package com.xavier.mozdeliveryapi.deliveryconfirmation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase.DCCAuditService;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase.DCCSecurityService;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase.DeliveryConfirmationService;
import com.xavier.mozdeliveryapi.deliveryconfirmation.application.usecase.DeliveryConfirmationServiceImpl;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.entity.DeliveryConfirmationCode;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCInvalidCodeException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.exception.DCCNotFoundException;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.repository.DeliveryConfirmationCodeRepository;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.service.DCCGenerationService;
import com.xavier.mozdeliveryapi.deliveryconfirmation.domain.service.DCCGenerationServiceImpl;
import com.xavier.mozdeliveryapi.deliveryconfirmation.infra.persistence.DeliveryConfirmationCodeRepositoryImpl;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.OrderId;

/**
 * Integration tests for DeliveryConfirmationService.
 */
class DeliveryConfirmationServiceTest {
    
    private DeliveryConfirmationService deliveryConfirmationService;
    private DeliveryConfirmationCodeRepository repository;
    private DCCGenerationService generationService;
    private DCCAuditService auditService;
    private DCCSecurityService securityService;
    
    @BeforeEach
    void setUp() {
        repository = new DeliveryConfirmationCodeRepositoryImpl();
        generationService = new DCCGenerationServiceImpl();
        auditService = mock(DCCAuditService.class);
        securityService = mock(DCCSecurityService.class);
        
        // Configure security service to allow validation attempts by default
        when(securityService.canCourierAttemptValidation(anyString(), any(OrderId.class))).thenReturn(true);
        when(securityService.detectSuspiciousActivity(anyString(), any(OrderId.class), anyString())).thenReturn(false);
        
        deliveryConfirmationService = new DeliveryConfirmationServiceImpl(
            repository, generationService, auditService, securityService);
    }
    
    @Test
    @DisplayName("Should generate and validate DCC successfully")
    void shouldGenerateAndValidateDCCSuccessfully() {
        // Given
        OrderId orderId = OrderId.generate();
        String courierId = "courier-123";
        
        // When - Generate code
        DeliveryConfirmationCode dcc = deliveryConfirmationService.generateCode(orderId);
        
        // Then - Code should be generated
        assertThat(dcc.getOrderId()).isEqualTo(orderId);
        assertThat(dcc.getCode()).matches("\\d{4}");
        assertThat(dcc.isActive()).isTrue();
        
        // When - Validate with correct code
        boolean isValid = deliveryConfirmationService.validateCode(orderId, dcc.getCode(), courierId);
        
        // Then - Validation should succeed
        assertThat(isValid).isTrue();
        
        // And - Code should be marked as used
        DeliveryConfirmationCode updatedDcc = deliveryConfirmationService.getCode(orderId);
        assertThat(updatedDcc.isUsed()).isTrue();
        assertThat(updatedDcc.isActive()).isFalse();
    }
    
    @Test
    @DisplayName("Should handle invalid code validation")
    void shouldHandleInvalidCodeValidation() {
        // Given
        OrderId orderId = OrderId.generate();
        String courierId = "courier-123";
        
        // When - Generate code
        DeliveryConfirmationCode dcc = deliveryConfirmationService.generateCode(orderId);
        
        // Then - Validation with wrong code should fail
        assertThatThrownBy(() -> deliveryConfirmationService.validateCode(orderId, "9999", courierId))
            .isInstanceOf(DCCInvalidCodeException.class);
        
        // And - Code should still be active with attempt recorded
        DeliveryConfirmationCode updatedDcc = deliveryConfirmationService.getCode(orderId);
        assertThat(updatedDcc.isActive()).isTrue();
        assertThat(updatedDcc.getAttemptCount()).isEqualTo(1);
        assertThat(updatedDcc.getRemainingAttempts()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should resend code successfully")
    void shouldResendCodeSuccessfully() {
        // Given
        OrderId orderId = OrderId.generate();
        
        // When - Generate initial code
        DeliveryConfirmationCode originalDcc = deliveryConfirmationService.generateCode(orderId);
        
        // And - Resend code
        DeliveryConfirmationCode newDcc = deliveryConfirmationService.resendCode(orderId);
        
        // Then - New code should be different and active
        assertThat(newDcc.getOrderId()).isEqualTo(orderId);
        assertThat(newDcc.getCode()).matches("\\d{4}");
        assertThat(newDcc.isActive()).isTrue();
        assertThat(newDcc.getGeneratedAt()).isAfter(originalDcc.getGeneratedAt());
    }
    
    @Test
    @DisplayName("Should force expire code")
    void shouldForceExpireCode() {
        // Given
        OrderId orderId = OrderId.generate();
        String adminId = "admin-456";
        String reason = "Customer requested cancellation";
        
        // When - Generate code
        deliveryConfirmationService.generateCode(orderId);
        
        // And - Force expire
        deliveryConfirmationService.forceExpireCode(orderId, adminId, reason);
        
        // Then - Code should be expired
        DeliveryConfirmationCode dcc = deliveryConfirmationService.getCode(orderId);
        assertThat(dcc.isActive()).isFalse();
        assertThat(dcc.getStatus().name()).isEqualTo("EXPIRED");
    }
    
    @Test
    @DisplayName("Should throw exception for non-existent code")
    void shouldThrowExceptionForNonExistentCode() {
        // Given
        OrderId orderId = OrderId.generate();
        
        // When & Then
        assertThatThrownBy(() -> deliveryConfirmationService.getCode(orderId))
            .isInstanceOf(DCCNotFoundException.class);
            
        assertThatThrownBy(() -> deliveryConfirmationService.validateCode(orderId, "1234", "courier-123"))
            .isInstanceOf(DCCNotFoundException.class);
    }
    
    @Test
    @DisplayName("Should replace existing active code when generating new one")
    void shouldReplaceExistingActiveCodeWhenGeneratingNewOne() {
        // Given
        OrderId orderId = OrderId.generate();
        
        // When - Generate first code
        DeliveryConfirmationCode firstDcc = deliveryConfirmationService.generateCode(orderId);
        
        // And - Generate second code for same order
        DeliveryConfirmationCode secondDcc = deliveryConfirmationService.generateCode(orderId);
        
        // Then - Second code should be active and different
        assertThat(secondDcc.getOrderId()).isEqualTo(orderId);
        assertThat(secondDcc.isActive()).isTrue();
        assertThat(secondDcc.getGeneratedAt()).isAfter(firstDcc.getGeneratedAt());
        
        // And - Retrieved code should be the second one
        DeliveryConfirmationCode retrievedDcc = deliveryConfirmationService.getCode(orderId);
        assertThat(retrievedDcc.getCode()).isEqualTo(secondDcc.getCode());
        assertThat(retrievedDcc.getGeneratedAt()).isEqualTo(secondDcc.getGeneratedAt());
    }
}