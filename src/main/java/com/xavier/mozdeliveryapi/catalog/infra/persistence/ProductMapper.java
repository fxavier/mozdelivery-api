package com.xavier.mozdeliveryapi.catalog.infra.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.xavier.mozdeliveryapi.catalog.domain.entity.Product;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CategoryId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductId;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductModifier;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductModifierOption;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductModifierType;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.StockInfo;
import com.xavier.mozdeliveryapi.catalog.infra.persistence.ProductEntity.ProductModifierData;
import com.xavier.mozdeliveryapi.catalog.infra.persistence.ProductEntity.ProductModifierOptionData;
import com.xavier.mozdeliveryapi.catalog.infra.persistence.ProductEntity.StockInfoData;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Currency;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Money;

/**
 * Mapper for converting between Product domain objects and ProductEntity persistence objects.
 */
@Component
public class ProductMapper {
    
    /**
     * Convert Product domain object to ProductEntity.
     */
    public ProductEntity toEntity(Product product, MerchantId merchantId) {
        return new ProductEntity(
            product.getProductId().value(),
            product.getCategoryId().value(),
            product.getName(),
            product.getDescription(),
            new ArrayList<>(product.getImageUrls()),
            product.getPrice().amount(),
            product.getCurrency().name(),
            product.getAvailability(),
            product.isVisible(),
            toStockInfoData(product.getStockInfo()),
            toModifierDataList(product.getModifiers()),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
    
    /**
     * Convert ProductEntity to Product domain object.
     */
    public Product toDomain(ProductEntity entity, MerchantId merchantId) {
        return new Product(
            ProductId.of(entity.getId()),
            merchantId,
            CategoryId.of(entity.getCategoryId()),
            entity.getName(),
            entity.getDescription(),
            entity.getImageUrls() != null ? new ArrayList<>(entity.getImageUrls()) : new ArrayList<>(),
            Money.of(entity.getBasePrice(), Currency.valueOf(entity.getCurrency())),
            entity.getAvailability(),
            fromStockInfoData(entity.getStockInfo()),
            fromModifierDataList(entity.getModifiers()),
            entity.getIsVisible(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * Convert StockInfo to StockInfoData.
     */
    private StockInfoData toStockInfoData(StockInfo stockInfo) {
        if (stockInfo == null) {
            return null;
        }
        
        return new StockInfoData(
            stockInfo.trackStock(),
            stockInfo.currentStock(),
            stockInfo.lowStockThreshold(),
            stockInfo.maxStock()
        );
    }
    
    /**
     * Convert StockInfoData to StockInfo.
     */
    private StockInfo fromStockInfoData(StockInfoData data) {
        if (data == null) {
            return StockInfo.noTracking();
        }
        
        return new StockInfo(
            data.getTrackStock() != null ? data.getTrackStock() : false,
            data.getCurrentStock(),
            data.getLowStockThreshold(),
            data.getMaxStock()
        );
    }
    
    /**
     * Convert list of ProductModifier to list of ProductModifierData.
     */
    private List<ProductModifierData> toModifierDataList(List<ProductModifier> modifiers) {
        if (modifiers == null) {
            return new ArrayList<>();
        }
        
        return modifiers.stream()
            .map(this::toModifierData)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert ProductModifier to ProductModifierData.
     */
    private ProductModifierData toModifierData(ProductModifier modifier) {
        return new ProductModifierData(
            modifier.name(),
            modifier.description(),
            modifier.type().name(),
            modifier.required(),
            toModifierOptionDataList(modifier.options())
        );
    }
    
    /**
     * Convert list of ProductModifierOption to list of ProductModifierOptionData.
     */
    private List<ProductModifierOptionData> toModifierOptionDataList(List<ProductModifierOption> options) {
        if (options == null) {
            return new ArrayList<>();
        }
        
        return options.stream()
            .map(this::toModifierOptionData)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert ProductModifierOption to ProductModifierOptionData.
     */
    private ProductModifierOptionData toModifierOptionData(ProductModifierOption option) {
        return new ProductModifierOptionData(
            option.name(),
            option.description(),
            option.priceAdjustment().amount(),
            option.priceAdjustment().currency().name(),
            option.available()
        );
    }
    
    /**
     * Convert list of ProductModifierData to list of ProductModifier.
     */
    private List<ProductModifier> fromModifierDataList(List<ProductModifierData> modifierDataList) {
        if (modifierDataList == null) {
            return new ArrayList<>();
        }
        
        return modifierDataList.stream()
            .map(this::fromModifierData)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert ProductModifierData to ProductModifier.
     */
    private ProductModifier fromModifierData(ProductModifierData data) {
        return new ProductModifier(
            data.getName(),
            data.getDescription(),
            ProductModifierType.valueOf(data.getType()),
            data.getRequired() != null ? data.getRequired() : false,
            fromModifierOptionDataList(data.getOptions())
        );
    }
    
    /**
     * Convert list of ProductModifierOptionData to list of ProductModifierOption.
     */
    private List<ProductModifierOption> fromModifierOptionDataList(List<ProductModifierOptionData> optionDataList) {
        if (optionDataList == null) {
            return new ArrayList<>();
        }
        
        return optionDataList.stream()
            .map(this::fromModifierOptionData)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert ProductModifierOptionData to ProductModifierOption.
     */
    private ProductModifierOption fromModifierOptionData(ProductModifierOptionData data) {
        return new ProductModifierOption(
            data.getName(),
            data.getDescription(),
            Money.of(data.getPriceAdjustment(), Currency.valueOf(data.getCurrency())),
            data.getAvailable() != null ? data.getAvailable() : true
        );
    }
}