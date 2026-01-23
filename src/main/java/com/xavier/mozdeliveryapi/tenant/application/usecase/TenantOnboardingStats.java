package com.xavier.mozdeliveryapi.tenant.application.usecase;

/**
 * Statistics about tenant onboarding.
 */
public record TenantOnboardingStats(
    long totalTenants,
    long activeTenants,
    long restaurantCount,
    long groceryCount,
    long pharmacyCount,
    long convenienceCount,
    long electronicsCount,
    long floristCount,
    long beveragesCount,
    long fuelStationCount
) {
    
    public double getActivePercentage() {
        return totalTenants > 0 ? (double) activeTenants / totalTenants * 100 : 0.0;
    }
    
    public long getVerticalCount(String vertical) {
        return switch (vertical.toUpperCase()) {
            case "RESTAURANT" -> restaurantCount;
            case "GROCERY" -> groceryCount;
            case "PHARMACY" -> pharmacyCount;
            case "CONVENIENCE" -> convenienceCount;
            case "ELECTRONICS" -> electronicsCount;
            case "FLORIST" -> floristCount;
            case "BEVERAGES" -> beveragesCount;
            case "FUEL_STATION" -> fuelStationCount;
            default -> 0L;
        };
    }
}