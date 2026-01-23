package com.xavier.mozdeliveryapi.catalog.domain.valueobject;

import java.util.List;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.ValueObject;

/**
 * Value object representing a product modifier (e.g., size, add-ons).
 */
public record ProductModifier(
    String name,
    String description,
    ProductModifierType type,
    boolean required,
    List<ProductModifierOption> options
) implements ValueObject {
    
    public ProductModifier {
        Objects.requireNonNull(name, "Modifier name cannot be null");
        Objects.requireNonNull(type, "Modifier type cannot be null");
        Objects.requireNonNull(options, "Modifier options cannot be null");
        
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Modifier name cannot be empty");
        }
        
        if (options.isEmpty()) {
            throw new IllegalArgumentException("Modifier must have at least one option");
        }
        
        // Validate single choice modifiers have unique options
        if (type == ProductModifierType.SINGLE_CHOICE) {
            long uniqueNames = options.stream()
                .map(ProductModifierOption::name)
                .distinct()
                .count();
            if (uniqueNames != options.size()) {
                throw new IllegalArgumentException("Single choice modifier options must have unique names");
            }
        }
    }
    
    /**
     * Create a single choice modifier (e.g., size: small, medium, large).
     */
    public static ProductModifier singleChoice(String name, String description, boolean required, 
                                             List<ProductModifierOption> options) {
        return new ProductModifier(name, description, ProductModifierType.SINGLE_CHOICE, required, options);
    }
    
    /**
     * Create a multiple choice modifier (e.g., toppings: cheese, bacon, etc.).
     */
    public static ProductModifier multipleChoice(String name, String description, boolean required, 
                                               List<ProductModifierOption> options) {
        return new ProductModifier(name, description, ProductModifierType.MULTIPLE_CHOICE, required, options);
    }
    
    /**
     * Check if modifier has a specific option.
     */
    public boolean hasOption(String optionName) {
        return options.stream()
            .anyMatch(option -> option.name().equals(optionName));
    }
    
    /**
     * Get option by name.
     */
    public ProductModifierOption getOption(String optionName) {
        return options.stream()
            .filter(option -> option.name().equals(optionName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Option not found: " + optionName));
    }
}