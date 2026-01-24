package com.xavier.mozdeliveryapi.order.application.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.order.domain.entity.Order;
import com.xavier.mozdeliveryapi.order.domain.valueobject.AgeVerification;
import com.xavier.mozdeliveryapi.order.domain.valueobject.OrderItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.Prescription;
import com.xavier.mozdeliveryapi.order.domain.valueobject.RestrictedItem;
import com.xavier.mozdeliveryapi.order.domain.valueobject.RestrictionType;
import com.xavier.mozdeliveryapi.order.domain.valueobject.ValidationResult;

/**
 * Implementation of VerticalBusinessRulesEngine.
 */
@Service
public class VerticalBusinessRulesEngineImpl implements VerticalBusinessRulesEngine {
    
    @Override
    public ValidationResult validateOrder(Order order, Vertical vertical) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(vertical, "Vertical cannot be null");
        
        ValidationResult itemsValidation = validateOrderItems(order.getItems(), vertical);
        
        // Additional vertical-specific validations can be added here
        return itemsValidation;
    }
    
    @Override
    public ValidationResult validateOrderItems(List<OrderItem> items, Vertical vertical) {
        Objects.requireNonNull(items, "Items cannot be null");
        Objects.requireNonNull(vertical, "Vertical cannot be null");
        
        List<ValidationResult.ValidationError> errors = new ArrayList<>();
        
        for (OrderItem item : items) {
            // Check if item is allowed in this vertical
            if (!isItemAllowedInVertical(item, vertical)) {
                errors.add(new ValidationResult.ValidationError(
                    "ITEM_NOT_ALLOWED_IN_VERTICAL",
                    String.format("Item %s is not allowed in %s vertical", item.productName(), vertical)
                ));
            }
            
            // Check for restricted items
            if (isRestrictedItem(item, vertical)) {
                RestrictedItem restrictedItem = getRestrictedItemInfo(item, vertical);
                if (restrictedItem.requiresPrescriptionValidation()) {
                    errors.add(new ValidationResult.ValidationError(
                        "PRESCRIPTION_REQUIRED",
                        String.format("Item %s requires a valid prescription", item.productName())
                    ));
                }
                
                if (restrictedItem.requiresAgeVerification()) {
                    errors.add(new ValidationResult.ValidationError(
                        "AGE_VERIFICATION_REQUIRED",
                        String.format("Item %s requires age verification (minimum age: %d)", 
                                    item.productName(), restrictedItem.minimumAge())
                    ));
                }
            }
        }
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
    
    @Override
    public ValidationResult validatePrescriptionRequirements(Order order, List<Prescription> prescriptions) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(prescriptions, "Prescriptions cannot be null");
        
        List<ValidationResult.ValidationError> errors = new ArrayList<>();
        List<RestrictedItem> restrictedItems = getRestrictedItems(order.getItems(), Vertical.PHARMACY);
        
        for (RestrictedItem restrictedItem : restrictedItems) {
            if (restrictedItem.requiresPrescriptionValidation()) {
                boolean prescriptionFound = prescriptions.stream()
                    .anyMatch(prescription -> 
                        prescription.medicationName().equalsIgnoreCase(restrictedItem.productName()) &&
                        prescription.isCurrentlyValid()
                    );
                
                if (!prescriptionFound) {
                    errors.add(new ValidationResult.ValidationError(
                        "MISSING_PRESCRIPTION",
                        String.format("Valid prescription required for %s", restrictedItem.productName())
                    ));
                }
            }
        }
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
    
    @Override
    public ValidationResult validateAgeVerification(Order order, AgeVerification ageVerification) {
        Objects.requireNonNull(order, "Order cannot be null");
        
        if (ageVerification == null) {
            if (requiresAgeVerification(order, Vertical.PHARMACY)) {
                return ValidationResult.invalid("AGE_VERIFICATION_MISSING", "Age verification is required");
            }
            return ValidationResult.valid();
        }
        
        if (!ageVerification.verified()) {
            return ValidationResult.invalid("AGE_VERIFICATION_NOT_VERIFIED", "Age verification is not verified");
        }
        
        List<ValidationResult.ValidationError> errors = new ArrayList<>();
        List<RestrictedItem> restrictedItems = getRestrictedItems(order.getItems(), Vertical.PHARMACY);
        
        for (RestrictedItem restrictedItem : restrictedItems) {
            if (restrictedItem.requiresAgeVerification()) {
                if (!restrictedItem.isAgeEligible(ageVerification.getCurrentAge())) {
                    errors.add(new ValidationResult.ValidationError(
                        "AGE_REQUIREMENT_NOT_MET",
                        String.format("Customer age (%d) does not meet minimum requirement (%d) for %s",
                                    ageVerification.getCurrentAge(), restrictedItem.minimumAge(), restrictedItem.productName())
                    ));
                }
            }
        }
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
    
    @Override
    public List<RestrictedItem> getRestrictedItems(List<OrderItem> items, Vertical vertical) {
        Objects.requireNonNull(items, "Items cannot be null");
        Objects.requireNonNull(vertical, "Vertical cannot be null");
        
        List<RestrictedItem> restrictedItems = new ArrayList<>();
        
        for (OrderItem item : items) {
            if (isRestrictedItem(item, vertical)) {
                restrictedItems.add(getRestrictedItemInfo(item, vertical));
            }
        }
        
        return restrictedItems;
    }
    
    @Override
    public boolean requiresPrescriptionValidation(Order order, Vertical vertical) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(vertical, "Vertical cannot be null");
        
        if (vertical != Vertical.PHARMACY) {
            return false;
        }
        
        return getRestrictedItems(order.getItems(), vertical).stream()
            .anyMatch(RestrictedItem::requiresPrescriptionValidation);
    }
    
    @Override
    public boolean requiresAgeVerification(Order order, Vertical vertical) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(vertical, "Vertical cannot be null");
        
        return getRestrictedItems(order.getItems(), vertical).stream()
            .anyMatch(RestrictedItem::requiresAgeVerification);
    }
    
    @Override
    public void applyVerticalProcessingRules(Order order, Vertical vertical) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(vertical, "Vertical cannot be null");
        
        // Apply vertical-specific processing rules
        switch (vertical) {
            case PHARMACY -> applyPharmacyRules(order);
            case RESTAURANT -> applyRestaurantRules(order);
            case GROCERY -> applyGroceryRules(order);
            case CONVENIENCE -> applyConvenienceRules(order);
            case ELECTRONICS -> applyElectronicsRules(order);
            case FLORIST -> applyFloristRules(order);
            case BEVERAGES -> applyBeveragesRules(order);
            case FUEL_STATION -> applyFuelStationRules(order);
        }
    }
    
    private boolean isItemAllowedInVertical(OrderItem item, Vertical vertical) {
        // For now, allow all items in all verticals
        // In real implementation, this would check against a product catalog
        return true;
    }
    
    private boolean isRestrictedItem(OrderItem item, Vertical vertical) {
        // For demonstration, consider items with "prescription" or "alcohol" in name as restricted
        String productName = item.productName().toLowerCase();
        
        return switch (vertical) {
            case PHARMACY -> productName.contains("prescription") || 
                           productName.contains("medicine") || 
                           productName.contains("drug");
            case BEVERAGES -> productName.contains("alcohol") || 
                            productName.contains("beer") || 
                            productName.contains("wine");
            default -> false;
        };
    }
    
    private RestrictedItem getRestrictedItemInfo(OrderItem item, Vertical vertical) {
        String productName = item.productName().toLowerCase();
        
        if (vertical == Vertical.PHARMACY) {
            if (productName.contains("prescription")) {
                return new RestrictedItem(
                    item.productId(),
                    item.productName(),
                    RestrictionType.PRESCRIPTION_REQUIRED,
                    0,
                    true,
                    java.util.Set.of("prescription"),
                    "Prescription medication"
                );
            } else {
                return new RestrictedItem(
                    item.productId(),
                    item.productName(),
                    RestrictionType.AGE_RESTRICTED,
                    16,
                    false,
                    java.util.Set.of("id"),
                    "Age-restricted medication"
                );
            }
        } else if (vertical == Vertical.BEVERAGES && productName.contains("alcohol")) {
            return new RestrictedItem(
                item.productId(),
                item.productName(),
                RestrictionType.ALCOHOL,
                18,
                false,
                java.util.Set.of("id"),
                "Alcoholic beverage"
            );
        }
        
        return new RestrictedItem(
            item.productId(),
            item.productName(),
            RestrictionType.NONE,
            0,
            false,
            java.util.Set.of(),
            "No restrictions"
        );
    }
    
    private void applyPharmacyRules(Order order) {
        // Pharmacy-specific rules
        // - Require prescription validation
        // - Age verification for certain medications
        // - Special handling for controlled substances
    }
    
    private void applyRestaurantRules(Order order) {
        // Restaurant-specific rules
        // - Food safety requirements
        // - Temperature control for delivery
        // - Special dietary restrictions
    }
    
    private void applyGroceryRules(Order order) {
        // Grocery-specific rules
        // - Perishable item handling
        // - Cold chain requirements
        // - Expiry date validation
    }
    
    private void applyConvenienceRules(Order order) {
        // Convenience store rules
        // - Age verification for tobacco/alcohol
        // - Limited delivery hours
    }
    
    private void applyElectronicsRules(Order order) {
        // Electronics-specific rules
        // - Warranty requirements
        // - Special packaging for fragile items
        // - Import/export restrictions
    }
    
    private void applyFloristRules(Order order) {
        // Florist-specific rules
        // - Delivery time sensitivity
        // - Special care instructions
        // - Seasonal availability
    }
    
    private void applyBeveragesRules(Order order) {
        // Beverages-specific rules
        // - Age verification for alcohol
        // - Temperature control requirements
        // - Glass container handling
    }
    
    private void applyFuelStationRules(Order order) {
        // Fuel station convenience store rules
        // - Age verification for tobacco/alcohol
        // - Limited product categories
        // - Safety requirements
    }
}