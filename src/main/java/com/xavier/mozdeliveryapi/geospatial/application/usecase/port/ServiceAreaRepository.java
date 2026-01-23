package com.xavier.mozdeliveryapi.geospatial.application.usecase.port;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.shared.application.usecase.port.Repository;

import java.util.List;
import java.util.Optional;
import com.xavier.mozdeliveryapi.geospatial.domain.entity.ServiceArea;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Boundary;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.City;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.ServiceAreaId;

/**
 * Repository interface for ServiceArea aggregate.
 * Provides methods for persisting and querying service areas with spatial operations.
 */
public interface ServiceAreaRepository extends Repository<ServiceArea, ServiceAreaId> {
    
    /**
     * Save a service area.
     * @param serviceArea the service area to save
     * @return the saved service area
     */
    ServiceArea save(ServiceArea serviceArea);
    
    /**
     * Find a service area by its ID.
     * @param id the service area ID
     * @return optional containing the service area if found
     */
    Optional<ServiceArea> findById(ServiceAreaId id);
    
    /**
     * Find all service areas for a specific tenant.
     * @param tenantId the tenant ID
     * @return list of service areas for the tenant
     */
    List<ServiceArea> findByTenantId(TenantId tenantId);
    
    /**
     * Find all active service areas for a specific tenant.
     * @param tenantId the tenant ID
     * @return list of active service areas for the tenant
     */
    List<ServiceArea> findActiveByTenantId(TenantId tenantId);
    
    /**
     * Find all service areas in a specific city.
     * @param city the city
     * @return list of service areas in the city
     */
    List<ServiceArea> findByCity(City city);
    
    /**
     * Find all active service areas in a specific city.
     * @param city the city
     * @return list of active service areas in the city
     */
    List<ServiceArea> findActiveByCity(City city);
    
    /**
     * Find service areas that contain a specific location.
     * @param location the location to search for
     * @return list of service areas containing the location
     */
    List<ServiceArea> findContainingLocation(Location location);
    
    /**
     * Find service areas for a specific tenant that contain a location.
     * @param location the location to search for
     * @param tenantId the tenant ID
     * @return list of service areas for the tenant containing the location
     */
    List<ServiceArea> findByTenantIdContainingLocation(TenantId tenantId, Location location);
    
    /**
     * Find service areas within a certain distance of a location.
     * @param location the center location
     * @param distance the search distance
     * @return list of service areas within the distance
     */
    List<ServiceArea> findWithinDistance(Location location, Distance distance);
    
    /**
     * Find service areas that intersect with a given boundary.
     * @param boundary the boundary to check for intersections
     * @return list of service areas that intersect with the boundary
     */
    List<ServiceArea> findIntersectingBoundary(Boundary boundary);
    
    /**
     * Find service areas for a tenant that intersect with a given boundary.
     * @param boundary the boundary to check for intersections
     * @param tenantId the tenant ID
     * @return list of service areas for the tenant that intersect with the boundary
     */
    List<ServiceArea> findByTenantIdIntersectingBoundary(TenantId tenantId, Boundary boundary);
    
    /**
     * Delete a service area.
     * @param serviceArea the service area to delete
     */
    void delete(ServiceArea serviceArea);
    
    /**
     * Delete a service area by ID.
     * @param id the service area ID
     */
    void deleteById(ServiceAreaId id);
}