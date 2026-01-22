package com.xavier.mozdeliveryapi.geospatial.infrastructure;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository interface for ServiceAreaEntity.
 * Provides spatial query methods using PostGIS functions.
 */
@Repository
public interface JpaServiceAreaRepository extends JpaRepository<ServiceAreaEntity, UUID> {
    
    /**
     * Find all service areas for a specific tenant.
     */
    List<ServiceAreaEntity> findByTenantId(UUID tenantId);
    
    /**
     * Find all active service areas for a specific tenant.
     */
    List<ServiceAreaEntity> findByTenantIdAndActiveTrue(UUID tenantId);
    
    /**
     * Find all service areas in a specific city.
     */
    List<ServiceAreaEntity> findByCity(String city);
    
    /**
     * Find all active service areas in a specific city.
     */
    List<ServiceAreaEntity> findByCityAndActiveTrue(String city);
    
    /**
     * Find service areas that contain a specific point using PostGIS ST_Contains function.
     */
    @Query("SELECT sa FROM ServiceAreaEntity sa WHERE ST_Contains(sa.boundary, :point) = true")
    List<ServiceAreaEntity> findContainingPoint(@Param("point") Geometry point);
    
    /**
     * Find active service areas that contain a specific point.
     */
    @Query("SELECT sa FROM ServiceAreaEntity sa WHERE sa.active = true AND ST_Contains(sa.boundary, :point) = true")
    List<ServiceAreaEntity> findActiveContainingPoint(@Param("point") Geometry point);
    
    /**
     * Find service areas for a specific tenant that contain a point.
     */
    @Query("SELECT sa FROM ServiceAreaEntity sa WHERE sa.tenantId = :tenantId AND sa.active = true AND ST_Contains(sa.boundary, :point) = true")
    List<ServiceAreaEntity> findByTenantIdContainingPoint(@Param("tenantId") UUID tenantId, @Param("point") Geometry point);
    
    /**
     * Find service areas within a certain distance of a point using PostGIS ST_DWithin function.
     */
    @Query(
            value = "SELECT * FROM service_areas sa " +
                    "WHERE ST_DWithin(sa.boundary, ST_GeomFromText(:point, 4326), :distance)",
            nativeQuery = true
    )
    List<ServiceAreaEntity> findWithinDistance(@Param("point") String point, @Param("distance") double distance);
    
    /**
     * Find active service areas within a certain distance of a point.
     */
    @Query(
            value = "SELECT * FROM service_areas sa " +
                    "WHERE sa.is_active = true " +
                    "AND ST_DWithin(sa.boundary, ST_GeomFromText(:point, 4326), :distance)",
            nativeQuery = true
    )
    List<ServiceAreaEntity> findActiveWithinDistance(@Param("point") String point, @Param("distance") double distance);
    
    /**
     * Find service areas that intersect with a given geometry using PostGIS ST_Intersects function.
     */
    @Query("SELECT sa FROM ServiceAreaEntity sa WHERE ST_Intersects(sa.boundary, :geometry) = true")
    List<ServiceAreaEntity> findIntersectingGeometry(@Param("geometry") Geometry geometry);
    
    /**
     * Find service areas for a tenant that intersect with a given geometry.
     */
    @Query("SELECT sa FROM ServiceAreaEntity sa WHERE sa.tenantId = :tenantId AND ST_Intersects(sa.boundary, :geometry) = true")
    List<ServiceAreaEntity> findByTenantIdIntersectingGeometry(@Param("tenantId") UUID tenantId, @Param("geometry") Geometry geometry);
    
    /**
     * Calculate the distance between a point and the nearest service area boundary.
     */
    @Query("SELECT MIN(ST_Distance(sa.boundary, :point)) FROM ServiceAreaEntity sa WHERE sa.active = true")
    Double findMinimumDistanceToServiceArea(@Param("point") Geometry point);
    
    /**
     * Find service areas ordered by distance from a point.
     */
    @Query("SELECT sa FROM ServiceAreaEntity sa WHERE sa.active = true ORDER BY ST_Distance(sa.boundary, :point)")
    List<ServiceAreaEntity> findOrderedByDistanceFromPoint(@Param("point") Geometry point);
}
