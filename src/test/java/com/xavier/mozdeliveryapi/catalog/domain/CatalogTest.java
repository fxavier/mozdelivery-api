package com.xavier.mozdeliveryapi.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;

/**
 * Unit tests for Catalog domain entity.
 */
class CatalogTest {
    
    @Test
    void shouldCreateCatalogWithValidData() {
        // Given
        CatalogId catalogId = CatalogId.generate();
        MerchantId merchantId = MerchantId.generate();
        String name = "Lunch Menu";
        String description = "Our delicious lunch offerings";
        
        // When
        Catalog catalog = new Catalog(catalogId, merchantId, name, description);
        
        // Then
        assertThat(catalog.getCatalogId()).isEqualTo(catalogId);
        assertThat(catalog.getMerchantId()).isEqualTo(merchantId);
        assertThat(catalog.getName()).isEqualTo(name);
        assertThat(catalog.getDescription()).isEqualTo(description);
        assertThat(catalog.getStatus()).isEqualTo(CatalogStatus.DRAFT);
        assertThat(catalog.isVisible()).isFalse();
        assertThat(catalog.canBeEdited()).isTrue();
        assertThat(catalog.hasCategories()).isFalse();
        assertThat(catalog.getCategoryCount()).isZero();
    }
    
    @Test
    void shouldRejectNullCatalogId() {
        // Given
        MerchantId merchantId = MerchantId.generate();
        
        // When & Then
        assertThatThrownBy(() -> new Catalog(null, merchantId, "Test", "Description"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Catalog ID cannot be null");
    }
    
    @Test
    void shouldRejectNullMerchantId() {
        // Given
        CatalogId catalogId = CatalogId.generate();
        
        // When & Then
        assertThatThrownBy(() -> new Catalog(catalogId, null, "Test", "Description"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Merchant ID cannot be null");
    }
    
    @Test
    void shouldRejectEmptyName() {
        // Given
        CatalogId catalogId = CatalogId.generate();
        MerchantId merchantId = MerchantId.generate();
        
        // When & Then
        assertThatThrownBy(() -> new Catalog(catalogId, merchantId, "", "Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Catalog name cannot be empty");
    }
    
    @Test
    void shouldUpdateCatalogDetails() {
        // Given
        Catalog catalog = createTestCatalog();
        String newName = "Updated Menu";
        String newDescription = "Updated description";
        
        // When
        catalog.updateDetails(newName, newDescription);
        
        // Then
        assertThat(catalog.getName()).isEqualTo(newName);
        assertThat(catalog.getDescription()).isEqualTo(newDescription);
    }
    
    @Test
    void shouldActivateCatalogWithCategories() {
        // Given
        Catalog catalog = createTestCatalog();
        catalog.addCategory(CategoryId.generate());
        
        // When
        catalog.activate();
        
        // Then
        assertThat(catalog.getStatus()).isEqualTo(CatalogStatus.ACTIVE);
        assertThat(catalog.isVisible()).isTrue();
    }
    
    @Test
    void shouldRejectActivationWithoutCategories() {
        // Given
        Catalog catalog = createTestCatalog();
        
        // When & Then
        assertThatThrownBy(catalog::activate)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot activate catalog without categories");
    }
    
    @Test
    void shouldDeactivateCatalog() {
        // Given
        Catalog catalog = createTestCatalog();
        catalog.addCategory(CategoryId.generate());
        catalog.activate();
        
        // When
        catalog.deactivate();
        
        // Then
        assertThat(catalog.getStatus()).isEqualTo(CatalogStatus.INACTIVE);
        assertThat(catalog.isVisible()).isFalse();
        assertThat(catalog.canBeEdited()).isTrue();
    }
    
    @Test
    void shouldArchiveCatalog() {
        // Given
        Catalog catalog = createTestCatalog();
        
        // When
        catalog.archive();
        
        // Then
        assertThat(catalog.getStatus()).isEqualTo(CatalogStatus.ARCHIVED);
        assertThat(catalog.isVisible()).isFalse();
        assertThat(catalog.canBeEdited()).isFalse();
    }
    
    @Test
    void shouldAddAndRemoveCategories() {
        // Given
        Catalog catalog = createTestCatalog();
        CategoryId categoryId = CategoryId.generate();
        
        // When
        catalog.addCategory(categoryId);
        
        // Then
        assertThat(catalog.hasCategories()).isTrue();
        assertThat(catalog.getCategoryCount()).isEqualTo(1);
        assertThat(catalog.getCategoryIds()).contains(categoryId);
        
        // When
        catalog.removeCategory(categoryId);
        
        // Then
        assertThat(catalog.hasCategories()).isFalse();
        assertThat(catalog.getCategoryCount()).isZero();
        assertThat(catalog.getCategoryIds()).doesNotContain(categoryId);
    }
    
    private Catalog createTestCatalog() {
        return new Catalog(
            CatalogId.generate(),
            MerchantId.generate(),
            "Test Catalog",
            "Test Description"
        );
    }
}