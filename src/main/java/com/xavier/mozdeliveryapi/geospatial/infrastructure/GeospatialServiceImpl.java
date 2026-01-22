package com.xavier.mozdeliveryapi.geospatial.infrastructure;

import com.xavier.mozdeliveryapi.geospatial.domain.*;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of GeospatialService using the ServiceAreaRepository.
 * Provides concrete implementations for all geospatial operations.
 */
@Service
public class GeospatialServiceImpl implements GeospatialService {
    
    private final ServiceAreaRepository serviceAreaRepository;
    private final DistanceCalculationService distanceCalculationService;
    
    public GeospatialServiceImpl(ServiceAreaRepository serviceAreaRepository,
                               DistanceCalculationService distanceCalculationService) {
        this.serviceAreaRepository = Objects.requireNonNull(serviceAreaRepository, 
            "ServiceAreaRepository cannot be null");
        this.distanceCalculationService = Objects.requireNonNull(distanceCalculationService,
            "DistanceCalculationService cannot be null");
    }
    
    @Override
    public boolean isWithinServiceArea(Location location, City city) {
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        
        List<ServiceArea> serviceAreas = serviceAreaRepository.findActiveByCity(city);
        return serviceAreas.stream()
                .anyMatch(serviceArea -> serviceArea.contains(location));
    }
    
    @Override
    public boolean isWithinTenantServiceArea(Location location, City city, TenantId tenantId) {
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(city, "City cannot be null");
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        List<ServiceArea> tenantServiceAreas = serviceAreaRepository.findActiveByTenantId(tenantId);
        return tenantServiceAreas.stream()
                .filter(serviceArea -> serviceArea.getCity().equals(city))
                .anyMatch(serviceArea -> serviceArea.contains(location));
    }
    
    @Override
    public Distance calculateDistance(Location from, Location to) {
        Objects.requireNonNull(from, "From location cannot be null");
        Objects.requireNonNull(to, "To location cannot be null");
        
        return distanceCalculationService.calculateStraightLineDistance(from, to);
    }
    
    @Override
    public List<TenantId> findTenantsServingLocation(Location location) {
        Objects.requireNonNull(location, "Location cannot be null");
        
        List<ServiceArea> serviceAreas = serviceAreaRepository.findContainingLocation(location);
        return serviceAreas.stream()
                .filter(ServiceArea::isActive)
                .map(ServiceArea::getTenantId)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TenantId> findNearbyTenants(Location location, Distance radius) {
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(radius, "Radius cannot be null");
        
        List<ServiceArea> nearbyServiceAreas = serviceAreaRepository.findWithinDistance(location, radius);
        return nearbyServiceAreas.stream()
                .filter(ServiceArea::isActive)
                .map(ServiceArea::getTenantId)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ServiceArea> getServiceAreasForCity(City city) {
        Objects.requireNonNull(city, "City cannot be null");
        
        return serviceAreaRepository.findActiveByCity(city);
    }
    
    @Override
    public List<ServiceArea> getServiceAreasForTenant(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        return serviceAreaRepository.findActiveByTenantId(tenantId);
    }
    
    @Override
    public boolean validateServiceAreaBoundaries(ServiceArea serviceArea) {
        Objects.requireNonNull(serviceArea, "Service area cannot be null");
        
        // Check for overlaps with existing service areas for the same tenant in the same city
        List<ServiceArea> existingServiceAreas = serviceAreaRepository.findByTenantIdIntersectingBoundary(
                serviceArea.getTenantId(), serviceArea.getBoundary());
        
        // Filter out the service area itself if it's being updated
        return existingServiceAreas.stream()
                .filter(existing -> !existing.getId().equals(serviceArea.getId()))
                .noneMatch(existing -> existing.getCity().equals(serviceArea.getCity()) && 
                                     existing.overlapsWith(serviceArea));
    }
}