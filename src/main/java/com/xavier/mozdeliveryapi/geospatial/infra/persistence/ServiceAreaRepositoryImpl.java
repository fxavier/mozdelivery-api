package com.xavier.mozdeliveryapi.geospatial.infra.persistence;

import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.xavier.mozdeliveryapi.geospatial.application.usecase.port.ServiceAreaRepository;
import com.xavier.mozdeliveryapi.geospatial.domain.entity.ServiceArea;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Boundary;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.City;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Distance;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.Location;
import com.xavier.mozdeliveryapi.geospatial.domain.valueobject.ServiceAreaId;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;

/**
 * Implementation of ServiceAreaRepository using JPA and PostGIS.
 * Provides concrete implementations for all spatial repository operations.
 */
@Repository
@ConditionalOnBean(JpaServiceAreaRepository.class)
@Transactional
public class ServiceAreaRepositoryImpl implements ServiceAreaRepository {
    
    private final JpaServiceAreaRepository jpaRepository;
    private final ServiceAreaMapper mapper;
    
    public ServiceAreaRepositoryImpl(JpaServiceAreaRepository jpaRepository, ServiceAreaMapper mapper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "JPA repository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "Mapper cannot be null");
    }
    
    @Override
    public ServiceArea save(ServiceArea serviceArea) {
        Objects.requireNonNull(serviceArea, "Service area cannot be null");
        
        ServiceAreaEntity entity = mapper.toEntity(serviceArea);
        ServiceAreaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceArea> findById(ServiceAreaId id) {
        Objects.requireNonNull(id, "Service area ID cannot be null");
        
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ServiceAreaId id) {
        Objects.requireNonNull(id, "Service area ID cannot be null");
        
        return jpaRepository.existsById(id.getValue());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        List<ServiceAreaEntity> entities = jpaRepository.findByTenantId(tenantId.value());
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findActiveByTenantId(TenantId tenantId) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        
        List<ServiceAreaEntity> entities = jpaRepository.findByTenantIdAndActiveTrue(tenantId.value());
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findByCity(City city) {
        Objects.requireNonNull(city, "City cannot be null");
        
        List<ServiceAreaEntity> entities = jpaRepository.findByCity(city.getName());
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findActiveByCity(City city) {
        Objects.requireNonNull(city, "City cannot be null");
        
        List<ServiceAreaEntity> entities = jpaRepository.findByCityAndActiveTrue(city.getName());
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findContainingLocation(Location location) {
        Objects.requireNonNull(location, "Location cannot be null");
        
        var point = mapper.locationToPointGeometry(location);
        List<ServiceAreaEntity> entities = jpaRepository.findActiveContainingPoint(point);
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findByTenantIdContainingLocation(TenantId tenantId, Location location) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        
        var point = mapper.locationToPointGeometry(location);
        List<ServiceAreaEntity> entities = jpaRepository.findByTenantIdContainingPoint(
                tenantId.value(), point);
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findWithinDistance(Location location, Distance distance) {
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(distance, "Distance cannot be null");
        
        String pointWKT = mapper.locationToWKTPoint(location);
        double distanceInMeters = distance.getMeters().doubleValue();
        
        List<ServiceAreaEntity> entities = jpaRepository.findActiveWithinDistance(pointWKT, distanceInMeters);
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findIntersectingBoundary(Boundary boundary) {
        Objects.requireNonNull(boundary, "Boundary cannot be null");
        
        var geometry = mapper.boundaryToGeometryForQuery(boundary);
        List<ServiceAreaEntity> entities = jpaRepository.findIntersectingGeometry(geometry);
        return mapper.toDomainList(entities);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServiceArea> findByTenantIdIntersectingBoundary(TenantId tenantId, Boundary boundary) {
        Objects.requireNonNull(tenantId, "Tenant ID cannot be null");
        Objects.requireNonNull(boundary, "Boundary cannot be null");
        
        var geometry = mapper.boundaryToGeometryForQuery(boundary);
        List<ServiceAreaEntity> entities = jpaRepository.findByTenantIdIntersectingGeometry(
                tenantId.value(), geometry);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public void delete(ServiceArea serviceArea) {
        Objects.requireNonNull(serviceArea, "Service area cannot be null");
        
        jpaRepository.deleteById(serviceArea.getId().getValue());
    }
    
    @Override
    public void deleteById(ServiceAreaId id) {
        Objects.requireNonNull(id, "Service area ID cannot be null");
        
        jpaRepository.deleteById(id.getValue());
    }
}
