package com.xavier.mozdeliveryapi.geospatial.domain;

import com.xavier.mozdeliveryapi.tenant.domain.TenantId;

import java.util.List;

/**
 * Domain service for geospatial operations.
 * Provides core geospatial functionality including location validation,
 * distance calculations, and service area queries.
 */
public interface GeospatialService {
    
    /**
     * Check if a location is within any service area for a given city.
     * @param location the location to check
     * @param city the city to check within
     * @return true if the location is within a service area
     */
    boolean isWithinServiceArea(Location location, City city);
    
    /**
     * Check if a location is within a specific tenant's service area in a city.
     * @param location the location to check
     * @param city the city to check within
     * @param tenantId the tenant to check for
     * @return true if the location is within the tenant's service area
     */
    boolean isWithinTenantServiceArea(Location location, City city, TenantId tenantId);
    
    /**
     * Calculate the straight-line distance between two locations.
     * @param from the starting location
     * @param to the destination location
     * @return the distance between the locations
     */
    Distance calculateDistance(Location from, Location to);
    
    /**
     * Find all tenants that serve a given location.
     * @param location the location to search for
     * @return list of tenant IDs that serve the location
     */
    List<TenantId> findTenantsServingLocation(Location location);
    
    /**
     * Find all tenants within a certain radius of a location.
     * @param location the center location
     * @param radius the search radius
     * @return list of tenant IDs within the radius
     */
    List<TenantId> findNearbyTenants(Location location, Distance radius);
    
    /**
     * Get all service areas for a specific city.
     * @param city the city to get service areas for
     * @return list of service areas in the city
     */
    List<ServiceArea> getServiceAreasForCity(City city);
    
    /**
     * Get all service areas for a specific tenant.
     * @param tenantId the tenant to get service areas for
     * @return list of service areas for the tenant
     */
    List<ServiceArea> getServiceAreasForTenant(TenantId tenantId);
    
    /**
     * Validate that a service area doesn't overlap with existing service areas
     * for the same tenant in the same city.
     * @param serviceArea the service area to validate
     * @return true if the service area is valid (no overlaps)
     */
    boolean validateServiceAreaBoundaries(ServiceArea serviceArea);
}