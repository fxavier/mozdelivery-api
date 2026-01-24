package com.xavier.mozdeliveryapi.catalog.domain;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Catalog;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Category;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Tag;

/**
 * Property-based test for catalog data isolation at the domain level.
 * 
 * **Property: Catalog Data Isolation**
 * **Validates: Requirements 3A**
 * 
 * This test verifies that catalog operations for one merchant do not affect
 * catalogs, categories, or products belonging to other merchants.
 */
@Tag("Feature: delivery-platform, Property: Catalog Data Isolation")
class CatalogDataIsolationPropertyTest {
    
    /**
     * Property: Catalog operations isolation.
     * 
     * Operations on catalogs belonging to one merchant should not affect
     * catalogs belonging to other merchants.
     */
    @Property(tries = 100)
    void catalogOperationsIsolation(
            @ForAll("validCatalogData") CatalogData merchant1CatalogData,
            @ForAll("validCatalogData") CatalogData merchant2CatalogData) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1CatalogData.merchantId().equals(merchant2CatalogData.merchantId()));
        
        // Create catalogs for different merchants
        Catalog merchant1Catalog = new Catalog(
            merchant1CatalogData.catalogId(),
            merchant1CatalogData.merchantId(),
            merchant1CatalogData.name(),
            merchant1CatalogData.description()
        );
        
        Catalog merchant2Catalog = new Catalog(
            merchant2CatalogData.catalogId(),
            merchant2CatalogData.merchantId(),
            merchant2CatalogData.name(),
            merchant2CatalogData.description()
        );
        
        // Add categories to both catalogs
        CategoryId merchant1CategoryId = CategoryId.generate();
        CategoryId merchant2CategoryId = CategoryId.generate();
        
        merchant1Catalog.addCategory(merchant1CategoryId);
        merchant2Catalog.addCategory(merchant2CategoryId);
        
        // Perform operations on merchant1's catalog
        merchant1Catalog.activate();
        merchant1Catalog.updateDetails("Updated Catalog 1", "Updated description 1");
        
        // Verify merchant2's catalog is unaffected
        assertThat(merchant2Catalog.getStatus()).isEqualTo(CatalogStatus.DRAFT);
        assertThat(merchant2Catalog.getName()).isEqualTo(merchant2CatalogData.name());
        assertThat(merchant2Catalog.getDescription()).isEqualTo(merchant2CatalogData.description());
        
        // Verify merchant1's catalog has the expected changes
        assertThat(merchant1Catalog.getStatus()).isEqualTo(CatalogStatus.ACTIVE);
        assertThat(merchant1Catalog.getName()).isEqualTo("Updated Catalog 1");
        assertThat(merchant1Catalog.getDescription()).isEqualTo("Updated description 1");
        
        // Verify merchant identities remain distinct
        assertThat(merchant1Catalog.getMerchantId()).isEqualTo(merchant1CatalogData.merchantId());
        assertThat(merchant2Catalog.getMerchantId()).isEqualTo(merchant2CatalogData.merchantId());
        assertThat(merchant1Catalog.getMerchantId()).isNotEqualTo(merchant2Catalog.getMerchantId());
        
        // Verify catalog identities remain distinct
        assertThat(merchant1Catalog.getCatalogId()).isEqualTo(merchant1CatalogData.catalogId());
        assertThat(merchant2Catalog.getCatalogId()).isEqualTo(merchant2CatalogData.catalogId());
        assertThat(merchant1Catalog.getCatalogId()).isNotEqualTo(merchant2Catalog.getCatalogId());
    }
    
    /**
     * Property: Category operations isolation.
     * 
     * Operations on categories belonging to one merchant should not affect
     * categories belonging to other merchants.
     */
    @Property(tries = 100)
    void categoryOperationsIsolation(
            @ForAll("validCategoryData") CategoryData merchant1CategoryData,
            @ForAll("validCategoryData") CategoryData merchant2CategoryData) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1CategoryData.merchantId().equals(merchant2CategoryData.merchantId()));
        
        // Create categories for different merchants
        Category merchant1Category = new Category(
            merchant1CategoryData.categoryId(),
            merchant1CategoryData.merchantId(),
            merchant1CategoryData.catalogId(),
            merchant1CategoryData.name(),
            merchant1CategoryData.description(),
            merchant1CategoryData.displayOrder()
        );
        
        Category merchant2Category = new Category(
            merchant2CategoryData.categoryId(),
            merchant2CategoryData.merchantId(),
            merchant2CategoryData.catalogId(),
            merchant2CategoryData.name(),
            merchant2CategoryData.description(),
            merchant2CategoryData.displayOrder()
        );
        
        // Perform operations on merchant1's category
        merchant1Category.updateDetails("Updated Category 1", "Updated description 1", "http://example.com/image1.jpg");
        merchant1Category.updateDisplayOrder(99);
        merchant1Category.hide();
        
        // Verify merchant2's category is unaffected
        assertThat(merchant2Category.getName()).isEqualTo(merchant2CategoryData.name());
        assertThat(merchant2Category.getDescription()).isEqualTo(merchant2CategoryData.description());
        assertThat(merchant2Category.getDisplayOrder()).isEqualTo(merchant2CategoryData.displayOrder());
        assertThat(merchant2Category.isVisible()).isTrue();
        
        // Verify merchant1's category has the expected changes
        assertThat(merchant1Category.getName()).isEqualTo("Updated Category 1");
        assertThat(merchant1Category.getDescription()).isEqualTo("Updated description 1");
        assertThat(merchant1Category.getDisplayOrder()).isEqualTo(99);
        assertThat(merchant1Category.isVisible()).isFalse();
        
        // Verify merchant identities remain distinct
        assertThat(merchant1Category.getMerchantId()).isEqualTo(merchant1CategoryData.merchantId());
        assertThat(merchant2Category.getMerchantId()).isEqualTo(merchant2CategoryData.merchantId());
        assertThat(merchant1Category.getMerchantId()).isNotEqualTo(merchant2Category.getMerchantId());
        
        // Verify category identities remain distinct
        assertThat(merchant1Category.getCategoryId()).isEqualTo(merchant1CategoryData.categoryId());
        assertThat(merchant2Category.getCategoryId()).isEqualTo(merchant2CategoryData.categoryId());
        assertThat(merchant1Category.getCategoryId()).isNotEqualTo(merchant2Category.getCategoryId());
    }
    
    /**
     * Property: Product operations isolation.
     * 
     * Operations on products belonging to one merchant should not affect
     * products belonging to other merchants.
     */
    @Property(tries = 100)
    void productOperationsIsolation(
            @ForAll("validProductData") ProductData merchant1ProductData,
            @ForAll("validProductData") ProductData merchant2ProductData) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1ProductData.merchantId().equals(merchant2ProductData.merchantId()));
        
        // Create products for different merchants
        Product merchant1Product = new Product(
            merchant1ProductData.productId(),
            merchant1ProductData.merchantId(),
            merchant1ProductData.categoryId(),
            merchant1ProductData.name(),
            merchant1ProductData.description(),
            merchant1ProductData.price()
        );
        
        Product merchant2Product = new Product(
            merchant2ProductData.productId(),
            merchant2ProductData.merchantId(),
            merchant2ProductData.categoryId(),
            merchant2ProductData.name(),
            merchant2ProductData.description(),
            merchant2ProductData.price()
        );
        
        // Perform operations on merchant1's product
        Money newPrice = Money.of(BigDecimal.valueOf(99.99), merchant1ProductData.price().currency());
        merchant1Product.updateDetails("Updated Product 1", "Updated description 1", newPrice);
        merchant1Product.updateImages(List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg"));
        merchant1Product.hide();
        
        // Verify merchant2's product is unaffected
        assertThat(merchant2Product.getName()).isEqualTo(merchant2ProductData.name());
        assertThat(merchant2Product.getDescription()).isEqualTo(merchant2ProductData.description());
        assertThat(merchant2Product.getPrice()).isEqualTo(merchant2ProductData.price());
        assertThat(merchant2Product.getImageUrls()).isEmpty();
        assertThat(merchant2Product.isVisible()).isTrue();
        
        // Verify merchant1's product has the expected changes
        assertThat(merchant1Product.getName()).isEqualTo("Updated Product 1");
        assertThat(merchant1Product.getDescription()).isEqualTo("Updated description 1");
        assertThat(merchant1Product.getPrice()).isEqualTo(newPrice);
        assertThat(merchant1Product.getImageUrls()).hasSize(2);
        assertThat(merchant1Product.isVisible()).isFalse();
        
        // Verify merchant identities remain distinct
        assertThat(merchant1Product.getMerchantId()).isEqualTo(merchant1ProductData.merchantId());
        assertThat(merchant2Product.getMerchantId()).isEqualTo(merchant2ProductData.merchantId());
        assertThat(merchant1Product.getMerchantId()).isNotEqualTo(merchant2Product.getMerchantId());
        
        // Verify product identities remain distinct
        assertThat(merchant1Product.getProductId()).isEqualTo(merchant1ProductData.productId());
        assertThat(merchant2Product.getProductId()).isEqualTo(merchant2ProductData.productId());
        assertThat(merchant1Product.getProductId()).isNotEqualTo(merchant2Product.getProductId());
    }
    
    /**
     * Property: Cross-merchant catalog hierarchy isolation.
     * 
     * Creating and managing catalog hierarchies (catalog -> category -> product)
     * for one merchant should not affect hierarchies of other merchants.
     */
    @Property(tries = 100)
    void catalogHierarchyIsolation(
            @ForAll("validMerchantId") MerchantId merchant1Id,
            @ForAll("validMerchantId") MerchantId merchant2Id) {
        
        // Ensure we have two different merchants
        Assume.that(!merchant1Id.equals(merchant2Id));
        
        // Create catalog hierarchies for both merchants
        // Merchant 1 hierarchy
        Catalog merchant1Catalog = new Catalog(CatalogId.generate(), merchant1Id, "Merchant 1 Catalog", "Description 1");
        CategoryId merchant1CategoryId = CategoryId.generate();
        merchant1Catalog.addCategory(merchant1CategoryId);
        
        Category merchant1Category = new Category(
            merchant1CategoryId, merchant1Id, merchant1Catalog.getCatalogId(),
            "Merchant 1 Category", "Category Description 1", 1
        );
        
        Product merchant1Product = new Product(
            ProductId.generate(), merchant1Id, merchant1CategoryId,
            "Merchant 1 Product", "Product Description 1",
            Money.of(BigDecimal.valueOf(10.00), Currency.USD)
        );
        
        // Merchant 2 hierarchy
        Catalog merchant2Catalog = new Catalog(CatalogId.generate(), merchant2Id, "Merchant 2 Catalog", "Description 2");
        CategoryId merchant2CategoryId = CategoryId.generate();
        merchant2Catalog.addCategory(merchant2CategoryId);
        
        Category merchant2Category = new Category(
            merchant2CategoryId, merchant2Id, merchant2Catalog.getCatalogId(),
            "Merchant 2 Category", "Category Description 2", 1
        );
        
        Product merchant2Product = new Product(
            ProductId.generate(), merchant2Id, merchant2CategoryId,
            "Merchant 2 Product", "Product Description 2",
            Money.of(BigDecimal.valueOf(20.00), Currency.MZN)
        );
        
        // Activate merchant1's catalog and modify its hierarchy
        merchant1Catalog.activate();
        merchant1Category.updateDisplayOrder(5);
        merchant1Product.updateDetails("Updated Product", "Updated Description", 
            Money.of(BigDecimal.valueOf(15.00), Currency.USD));
        
        // Verify merchant2's hierarchy is completely unaffected
        assertThat(merchant2Catalog.getStatus()).isEqualTo(CatalogStatus.DRAFT);
        assertThat(merchant2Catalog.getName()).isEqualTo("Merchant 2 Catalog");
        assertThat(merchant2Category.getDisplayOrder()).isEqualTo(1);
        assertThat(merchant2Category.getName()).isEqualTo("Merchant 2 Category");
        assertThat(merchant2Product.getName()).isEqualTo("Merchant 2 Product");
        assertThat(merchant2Product.getPrice().amount()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
        assertThat(merchant2Product.getCurrency()).isEqualTo(Currency.MZN);
        
        // Verify merchant1's hierarchy has the expected changes
        assertThat(merchant1Catalog.getStatus()).isEqualTo(CatalogStatus.ACTIVE);
        assertThat(merchant1Category.getDisplayOrder()).isEqualTo(5);
        assertThat(merchant1Product.getName()).isEqualTo("Updated Product");
        assertThat(merchant1Product.getPrice().amount()).isEqualByComparingTo(BigDecimal.valueOf(15.00));
        
        // Verify all entities maintain correct merchant associations
        assertThat(merchant1Catalog.getMerchantId()).isEqualTo(merchant1Id);
        assertThat(merchant1Category.getMerchantId()).isEqualTo(merchant1Id);
        assertThat(merchant1Product.getMerchantId()).isEqualTo(merchant1Id);
        
        assertThat(merchant2Catalog.getMerchantId()).isEqualTo(merchant2Id);
        assertThat(merchant2Category.getMerchantId()).isEqualTo(merchant2Id);
        assertThat(merchant2Product.getMerchantId()).isEqualTo(merchant2Id);
        
        // Verify no cross-contamination of IDs
        assertThat(merchant1Category.getCatalogId()).isEqualTo(merchant1Catalog.getCatalogId());
        assertThat(merchant2Category.getCatalogId()).isEqualTo(merchant2Catalog.getCatalogId());
        assertThat(merchant1Category.getCatalogId()).isNotEqualTo(merchant2Category.getCatalogId());
        
        assertThat(merchant1Product.getCategoryId()).isEqualTo(merchant1CategoryId);
        assertThat(merchant2Product.getCategoryId()).isEqualTo(merchant2CategoryId);
        assertThat(merchant1Product.getCategoryId()).isNotEqualTo(merchant2Product.getCategoryId());
    }
    
    // Generators for test data
    
    @Provide
    Arbitrary<MerchantId> validMerchantId() {
        return Arbitraries.create(MerchantId::generate);
    }
    
    @Provide
    Arbitrary<CatalogData> validCatalogData() {
        return Combinators.combine(
            Arbitraries.create(CatalogId::generate),
            Arbitraries.create(MerchantId::generate),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(200)
        ).as(CatalogData::new);
    }
    
    @Provide
    Arbitrary<CategoryData> validCategoryData() {
        return Combinators.combine(
            Arbitraries.create(CategoryId::generate),
            Arbitraries.create(MerchantId::generate),
            Arbitraries.create(CatalogId::generate),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(200),
            Arbitraries.integers().between(0, 100)
        ).as(CategoryData::new);
    }
    
    @Provide
    Arbitrary<ProductData> validProductData() {
        return Combinators.combine(
            Arbitraries.create(ProductId::generate),
            Arbitraries.create(MerchantId::generate),
            Arbitraries.create(CategoryId::generate),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(100),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(500),
            validMoney()
        ).as(ProductData::new);
    }
    
    @Provide
    Arbitrary<Money> validMoney() {
        return Combinators.combine(
            Arbitraries.bigDecimals().between(BigDecimal.valueOf(0.01), BigDecimal.valueOf(1000.00)),
            Arbitraries.of(Currency.class)
        ).as(Money::of);
    }
    
    // Data classes for test parameters
    
    record CatalogData(CatalogId catalogId, MerchantId merchantId, String name, String description) {}
    
    record CategoryData(CategoryId categoryId, MerchantId merchantId, CatalogId catalogId, 
                       String name, String description, int displayOrder) {}
    
    record ProductData(ProductId productId, MerchantId merchantId, CategoryId categoryId, 
                      String name, String description, Money price) {}
}