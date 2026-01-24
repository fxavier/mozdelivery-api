package com.xavier.mozdeliveryapi.catalog.application.usecase;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xavier.mozdeliveryapi.catalog.application.usecase.port.ProductRepository;
import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;
import com.xavier.mozdeliveryapi.catalog.domain.exception.ProductNotFoundException;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.StockInfo;
import com.xavier.mozdeliveryapi.catalog.infra.persistence.CategoryRepositoryHelper;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

/**
 * Implementation of ProductService for product domain operations.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepositoryHelper categoryHelper;
    
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepositoryHelper categoryHelper) {
        this.productRepository = Objects.requireNonNull(productRepository, "ProductRepository cannot be null");
        this.categoryHelper = Objects.requireNonNull(categoryHelper, "CategoryRepositoryHelper cannot be null");
    }
    
    @Override
    public Product createProduct(MerchantId merchantId, CategoryId categoryId, String name, String description, Money price, StockInfo stockInfo) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        Objects.requireNonNull(name, "Product name cannot be null");
        Objects.requireNonNull(price, "Product price cannot be null");
        Objects.requireNonNull(stockInfo, "Stock info cannot be null");
        
        // If merchantId is null, resolve it from category
        MerchantId resolvedMerchantId = merchantId;
        if (resolvedMerchantId == null) {
            resolvedMerchantId = categoryHelper.getMerchantIdForCategory(categoryId);
        }
        
        ProductId productId = ProductId.generate();
        Product product = new Product(productId, resolvedMerchantId, categoryId, name, description, price);
        
        // Update stock info if different from default
        if (!stockInfo.equals(StockInfo.noTracking())) {
            product.updateStockInfo(stockInfo);
        }
        
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProduct(ProductId productId, String name, String description, List<String> imageUrls) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(name, "Product name cannot be null");
        Objects.requireNonNull(imageUrls, "Image URLs cannot be null");
        
        Product product = getProduct(productId);
        
        // Update basic details (price remains the same)
        product.updateDetails(name, description, product.getPrice());
        
        // Update images
        product.updateImages(imageUrls);
        
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProductAvailability(ProductId productId, ProductAvailability availability) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(availability, "Availability cannot be null");
        
        Product product = getProduct(productId);
        product.updateAvailability(availability);
        
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProductStock(ProductId productId, StockInfo stockInfo) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(stockInfo, "Stock info cannot be null");
        
        Product product = getProduct(productId);
        product.updateStockInfo(stockInfo);
        
        return productRepository.save(product);
    }
    
    @Override
    public Product showProduct(ProductId productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        Product product = getProduct(productId);
        product.show();
        
        return productRepository.save(product);
    }
    
    @Override
    public Product hideProduct(ProductId productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        Product product = getProduct(productId);
        product.hide();
        
        return productRepository.save(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Product getProduct(ProductId productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        return productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getCategoryProducts(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        return productRepository.findByCategoryId(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAvailableCategoryProducts(CategoryId categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        
        return productRepository.findAvailableByCategoryId(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(MerchantId merchantId) {
        Objects.requireNonNull(merchantId, "Merchant ID cannot be null");
        
        return productRepository.findLowStockByMerchantId(merchantId);
    }
    
    @Override
    public void deleteProduct(ProductId productId) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
        
        productRepository.deleteById(productId);
    }
}