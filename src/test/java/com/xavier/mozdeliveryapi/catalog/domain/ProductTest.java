package com.xavier.mozdeliveryapi.catalog.domain;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.StockInfo;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

/**
 * Unit tests for Product domain entity.
 */
class ProductTest {
    
    @Test
    void shouldCreateProductWithValidData() {
        // Given
        ProductId productId = ProductId.generate();
        MerchantId merchantId = MerchantId.generate();
        CategoryId categoryId = CategoryId.generate();
        String name = "Cheeseburger";
        String description = "Delicious beef burger with cheese";
        Money price = Money.of(BigDecimal.valueOf(12.99), Currency.USD);
        
        // When
        Product product = new Product(productId, merchantId, categoryId, name, description, price);
        
        // Then
        assertThat(product.getProductId()).isEqualTo(productId);
        assertThat(product.getMerchantId()).isEqualTo(merchantId);
        assertThat(product.getCategoryId()).isEqualTo(categoryId);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getCurrency()).isEqualTo(Currency.USD);
        assertThat(product.getAvailability()).isEqualTo(ProductAvailability.AVAILABLE);
        assertThat(product.isVisible()).isTrue();
        assertThat(product.canBeOrdered()).isTrue();
        assertThat(product.getStockInfo().trackStock()).isFalse();
    }
    
    @Test
    void shouldRejectNullProductId() {
        // Given
        MerchantId merchantId = MerchantId.generate();
        CategoryId categoryId = CategoryId.generate();
        Money price = Money.of(BigDecimal.valueOf(10.00), Currency.USD);
        
        // When & Then
        assertThatThrownBy(() -> new Product(null, merchantId, categoryId, "Test", "Description", price))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Product ID cannot be null");
    }
    
    @Test
    void shouldRejectEmptyName() {
        // Given
        ProductId productId = ProductId.generate();
        MerchantId merchantId = MerchantId.generate();
        CategoryId categoryId = CategoryId.generate();
        Money price = Money.of(BigDecimal.valueOf(10.00), Currency.USD);
        
        // When & Then
        assertThatThrownBy(() -> new Product(productId, merchantId, categoryId, "", "Description", price))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Product name cannot be empty");
    }
    
    @Test
    void shouldUpdateProductDetails() {
        // Given
        Product product = createTestProduct();
        String newName = "Updated Burger";
        String newDescription = "Updated description";
        Money newPrice = Money.of(BigDecimal.valueOf(15.99), Currency.USD);
        
        // When
        product.updateDetails(newName, newDescription, newPrice);
        
        // Then
        assertThat(product.getName()).isEqualTo(newName);
        assertThat(product.getDescription()).isEqualTo(newDescription);
        assertThat(product.getPrice()).isEqualTo(newPrice);
    }
    
    @Test
    void shouldUpdateAvailability() {
        // Given
        Product product = createTestProduct();
        
        // When
        product.updateAvailability(ProductAvailability.OUT_OF_STOCK);
        
        // Then
        assertThat(product.getAvailability()).isEqualTo(ProductAvailability.OUT_OF_STOCK);
        assertThat(product.canBeOrdered()).isFalse();
    }
    
    @Test
    void shouldTrackStock() {
        // Given
        Product product = createTestProduct();
        StockInfo stockInfo = StockInfo.tracked(100, 10, 200);
        
        // When
        product.updateStockInfo(stockInfo);
        
        // Then
        assertThat(product.getStockInfo().trackStock()).isTrue();
        assertThat(product.getStockInfo().currentStock()).isEqualTo(100);
        assertThat(product.getStockInfo().lowStockThreshold()).isEqualTo(10);
        assertThat(product.getStockInfo().maxStock()).isEqualTo(200);
        assertThat(product.isLowStock()).isFalse();
    }
    
    @Test
    void shouldReduceStock() {
        // Given
        Product product = createTestProduct();
        StockInfo stockInfo = StockInfo.tracked(100, 10, 200);
        product.updateStockInfo(stockInfo);
        
        // When
        product.reduceStock(20);
        
        // Then
        assertThat(product.getStockInfo().currentStock()).isEqualTo(80);
        assertThat(product.canBeOrdered()).isTrue();
    }
    
    @Test
    void shouldAddStock() {
        // Given
        Product product = createTestProduct();
        StockInfo stockInfo = StockInfo.tracked(50, 10, 200);
        product.updateStockInfo(stockInfo);
        
        // When
        product.addStock(30);
        
        // Then
        assertThat(product.getStockInfo().currentStock()).isEqualTo(80);
    }
    
    @Test
    void shouldAutoUpdateAvailabilityWhenOutOfStock() {
        // Given
        Product product = createTestProduct();
        StockInfo stockInfo = StockInfo.tracked(1, 10, 200);
        product.updateStockInfo(stockInfo);
        
        // When
        product.reduceStock(1);
        
        // Then
        assertThat(product.getStockInfo().currentStock()).isZero();
        assertThat(product.getAvailability()).isEqualTo(ProductAvailability.OUT_OF_STOCK);
        assertThat(product.canBeOrdered()).isFalse();
    }
    
    @Test
    void shouldAutoUpdateAvailabilityWhenBackInStock() {
        // Given
        Product product = createTestProduct();
        StockInfo stockInfo = StockInfo.tracked(0, 10, 200);
        product.updateStockInfo(stockInfo);
        product.updateAvailability(ProductAvailability.OUT_OF_STOCK);
        
        // When
        product.addStock(5);
        
        // Then
        assertThat(product.getStockInfo().currentStock()).isEqualTo(5);
        assertThat(product.getAvailability()).isEqualTo(ProductAvailability.AVAILABLE);
        assertThat(product.canBeOrdered()).isTrue();
    }
    
    @Test
    void shouldHideAndShowProduct() {
        // Given
        Product product = createTestProduct();
        
        // When
        product.hide();
        
        // Then
        assertThat(product.isVisible()).isFalse();
        assertThat(product.canBeOrdered()).isFalse();
        
        // When
        product.show();
        
        // Then
        assertThat(product.isVisible()).isTrue();
        assertThat(product.canBeOrdered()).isTrue();
    }
    
    private Product createTestProduct() {
        return new Product(
            ProductId.generate(),
            MerchantId.generate(),
            CategoryId.generate(),
            "Test Product",
            "Test Description",
            Money.of(BigDecimal.valueOf(10.00), Currency.USD)
        );
    }
}