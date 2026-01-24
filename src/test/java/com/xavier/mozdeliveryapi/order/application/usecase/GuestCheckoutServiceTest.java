package com.xavier.mozdeliveryapi.order.application.usecase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.xavier.mozdeliveryapi.order.application.usecase.port.OrderRepository;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestInfo;
import com.xavier.mozdeliveryapi.order.domain.valueobject.GuestTrackingToken;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;

/**
 * Unit tests for GuestCheckoutService implementation.
 */
@ExtendWith(MockitoExtension.class)
class GuestCheckoutServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    private GuestCheckoutService guestCheckoutService;
    
    @BeforeEach
    void setUp() {
        guestCheckoutService = new GuestCheckoutServiceImpl(orderRepository);
    }
    
    @Test
    void shouldGenerateGuestInfo() {
        // Given
        String contactPhone = "+258123456789";
        String contactEmail = "guest@example.com";
        String contactName = "John Doe";
        
        // When
        GuestInfo guestInfo = guestCheckoutService.generateGuestInfo(contactPhone, contactEmail, contactName);
        
        // Then
        assertThat(guestInfo).isNotNull();
        assertThat(guestInfo.contactPhone()).isEqualTo(contactPhone);
        assertThat(guestInfo.contactEmail()).isEqualTo(contactEmail);
        assertThat(guestInfo.contactName()).isEqualTo(contactName);
        assertThat(guestInfo.trackingToken()).isNotNull();
        assertThat(guestInfo.trackingToken().isValid()).isTrue();
    }
    
    @Test
    void shouldFindOrderByTrackingToken() {
        // Given
        GuestTrackingToken token = GuestTrackingToken.generate();
        Order mockOrder = createMockOrder();
        when(orderRepository.findByGuestTrackingToken(token)).thenReturn(Optional.of(mockOrder));
        
        // When
        Order foundOrder = guestCheckoutService.findOrderByTrackingToken(token);
        
        // Then
        assertThat(foundOrder).isEqualTo(mockOrder);
        verify(orderRepository).findByGuestTrackingToken(token);
    }
    
    @Test
    void shouldThrowExceptionWhenOrderNotFoundByToken() {
        // Given
        GuestTrackingToken token = GuestTrackingToken.generate();
        when(orderRepository.findByGuestTrackingToken(token)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> guestCheckoutService.findOrderByTrackingToken(token))
            .isInstanceOf(com.xavier.mozdeliveryapi.order.domain.exception.OrderNotFoundException.class);
    }
    
    @Test
    void shouldThrowExceptionWhenTokenExpired() {
        // Given
        GuestTrackingToken expiredToken = GuestTrackingToken.of(
            "expired-token",
            java.time.Instant.now().minusSeconds(7200), // 2 hours ago
            java.time.Instant.now().minusSeconds(3600)  // 1 hour ago (expired)
        );
        
        // When & Then
        assertThatThrownBy(() -> guestCheckoutService.findOrderByTrackingToken(expiredToken))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("expired");
    }
    
    @Test
    void shouldValidateGuestOrderCreation() {
        // Given
        GuestCheckoutService.GuestOrderCommand validCommand = createValidGuestOrderCommand();
        
        // When & Then
        assertThatCode(() -> guestCheckoutService.validateGuestOrderCreation(validCommand))
            .doesNotThrowAnyException();
    }
    
    @Test
    void shouldThrowExceptionForInvalidGuestOrderCommand() {
        // Given - command with empty items
        GuestCheckoutService.GuestOrderCommand invalidCommand = new GuestCheckoutService.GuestOrderCommand(
            TenantId.generate(),
            createValidGuestInfo(),
            java.util.Collections.emptyList(), // Empty items
            createValidDeliveryAddress(),
            createValidPaymentInfo()
        );
        
        // When & Then
        assertThatThrownBy(() -> guestCheckoutService.validateGuestOrderCreation(invalidCommand))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("at least one item");
    }
    
    private Order createMockOrder() {
        return mock(Order.class);
    }
    
    private GuestCheckoutService.GuestOrderCommand createValidGuestOrderCommand() {
        return new GuestCheckoutService.GuestOrderCommand(
            TenantId.generate(),
            createValidGuestInfo(),
            java.util.List.of(createValidOrderItem()),
            createValidDeliveryAddress(),
            createValidPaymentInfo()
        );
    }
    
    private GuestInfo createValidGuestInfo() {
        return GuestInfo.create("+258123456789", "guest@example.com", "John Doe");
    }
    
    private com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem createValidOrderItem() {
        return com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem.of(
            "product-123", // String productId, not ProductId object
            "Test Product",
            1,
            com.xavier.mozdeliveryapi.shared.domain.valueobject.Money.of(
                java.math.BigDecimal.valueOf(10.00),
                com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency.USD
            )
        );
    }
    
    private com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress createValidDeliveryAddress() {
        return com.xavier.mozdeliveryapi.order.domain.valueobject.DeliveryAddress.of(
            "123 Test Street",
            "Test City",
            "Test State", 
            "12345",
            "Test Country",
            -25.9692, // latitude
            32.5732   // longitude
        );
    }
    
    private com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo createValidPaymentInfo() {
        return com.xavier.mozdeliveryapi.order.domain.valueobject.PaymentInfo.pending(
            com.xavier.mozdeliveryapi.shared.domain.valueobject.PaymentMethod.CREDIT_CARD,
            com.xavier.mozdeliveryapi.shared.domain.valueobject.Money.of(
                java.math.BigDecimal.valueOf(10.00),
                com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency.USD
            )
        );
    }
}