package com.xavier.mozdeliveryapi.geospatial.infrastructure;

import com.xavier.mozdeliveryapi.geospatial.domain.*;
import com.xavier.mozdeliveryapi.tenant.domain.TenantId;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class to convert between ServiceArea domain objects and ServiceAreaEntity JPA entities.
 * Handles the conversion of spatial data between domain objects and PostGIS geometry types.
 */
@Component
public class ServiceAreaMapper {
    
    private final GeometryFactory geometryFactory;
    private final WKTReader wktReader;
    private final WKTWriter wktWriter;
    
    public ServiceAreaMapper() {
        this.geometryFactory = new GeometryFactory();
        this.wktReader = new WKTReader(geometryFactory);
        this.wktWriter = new WKTWriter();
    }
    
    /**
     * Convert ServiceArea domain object to ServiceAreaEntity.
     */
    public ServiceAreaEntity toEntity(ServiceArea serviceArea) {
        if (serviceArea == null) {
            return null;
        }
        
        City city = serviceArea.getCity();
        Geometry boundary = boundaryToGeometry(serviceArea.getBoundary());
        
        return new ServiceAreaEntity(
                serviceArea.getId().getValue(),
                serviceArea.getTenantId().value(),
                city.getName(),
                city.getCountryCode(),
                city.getCenterLocation().getLatitude(),
                city.getCenterLocation().getLongitude(),
                boundary,
                serviceArea.isActive(),
                serviceArea.getCreatedAt(),
                serviceArea.getUpdatedAt()
        );
    }
    
    /**
     * Convert ServiceAreaEntity to ServiceArea domain object.
     */
    public ServiceArea toDomain(ServiceAreaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ServiceAreaId id = ServiceAreaId.of(entity.getId());
        TenantId tenantId = TenantId.of(entity.getTenantId());
        
        Location centerLocation = Location.of(
                entity.getCityCenterLatitude(),
                entity.getCityCenterLongitude()
        );
        
        City city = City.of(entity.getCity(), entity.getCountryCode(), centerLocation);
        Boundary boundary = geometryToBoundary(entity.getBoundary());
        
        return ServiceArea.reconstitute(
                id,
                tenantId,
                city,
                boundary,
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    
    /**
     * Convert list of ServiceAreaEntity to list of ServiceArea domain objects.
     */
    public List<ServiceArea> toDomainList(List<ServiceAreaEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }
    
    /**
     * Convert Boundary domain object to PostGIS Geometry.
     */
    private Geometry boundaryToGeometry(Boundary boundary) {
        List<Location> vertices = boundary.getVertices();
        Coordinate[] coordinates = new Coordinate[vertices.size() + 1]; // +1 to close the polygon
        
        for (int i = 0; i < vertices.size(); i++) {
            Location vertex = vertices.get(i);
            coordinates[i] = new Coordinate(
                    vertex.getLongitude().doubleValue(),
                    vertex.getLatitude().doubleValue()
            );
        }
        
        // Close the polygon by repeating the first coordinate
        coordinates[vertices.size()] = coordinates[0];
        
        LinearRing shell = geometryFactory.createLinearRing(coordinates);
        return geometryFactory.createPolygon(shell);
    }
    
    /**
     * Convert PostGIS Geometry to Boundary domain object.
     */
    private Boundary geometryToBoundary(Geometry geometry) {
        if (!(geometry instanceof Polygon polygon)) {
            throw new IllegalArgumentException("Geometry must be a Polygon");
        }
        
        Coordinate[] coordinates = polygon.getExteriorRing().getCoordinates();
        List<Location> vertices = new ArrayList<>();
        
        // Skip the last coordinate as it's a duplicate of the first (polygon closure)
        for (int i = 0; i < coordinates.length - 1; i++) {
            Coordinate coord = coordinates[i];
            Location location = Location.of(coord.y, coord.x); // Note: y=lat, x=lon
            vertices.add(location);
        }
        
        return Boundary.of(vertices);
    }
    
    /**
     * Convert Location to WKT Point string for spatial queries.
     */
    public String locationToWKTPoint(Location location) {
        if (location == null) {
            return null;
        }
        
        return String.format("POINT(%s %s)", 
                location.getLongitude().doubleValue(),
                location.getLatitude().doubleValue());
    }

    /**
     * Convert Location to Geometry Point for spatial queries.
     */
    public Geometry locationToPointGeometry(Location location) {
        if (location == null) {
            return null;
        }

        var point = geometryFactory.createPoint(new Coordinate(
                location.getLongitude().doubleValue(),
                location.getLatitude().doubleValue()
        ));
        point.setSRID(4326);
        return point;
    }
    
    /**
     * Convert Boundary to PostGIS Geometry for spatial queries.
     */
    public Geometry boundaryToGeometryForQuery(Boundary boundary) {
        return boundaryToGeometry(boundary);
    }
    
    /**
     * Parse WKT string to Geometry.
     */
    public Geometry parseWKT(String wkt) {
        try {
            return wktReader.read(wkt);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid WKT: " + wkt, e);
        }
    }
    
    /**
     * Convert Geometry to WKT string.
     */
    public String geometryToWKT(Geometry geometry) {
        return wktWriter.write(geometry);
    }
}
