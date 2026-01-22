package com.xavier.mozdeliveryapi.geospatial.domain;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Boundary value object representing a polygon boundary for service areas.
 * Uses a list of locations to define the polygon vertices.
 */
public final class Boundary implements ValueObject {
    
    @NotNull
    @Size(min = 3, message = "Boundary must have at least 3 points to form a polygon")
    private final List<Location> vertices;
    
    private Boundary(List<Location> vertices) {
        this.vertices = Collections.unmodifiableList(new ArrayList<>(vertices));
    }
    
    public static Boundary of(List<Location> vertices) {
        Objects.requireNonNull(vertices, "Vertices cannot be null");
        
        if (vertices.size() < 3) {
            throw new IllegalArgumentException("Boundary must have at least 3 points to form a polygon");
        }
        
        // Validate that all vertices are valid locations
        for (Location vertex : vertices) {
            Objects.requireNonNull(vertex, "Vertex cannot be null");
        }
        
        return new Boundary(vertices);
    }
    
    public List<Location> getVertices() {
        return vertices;
    }
    
    /**
     * Check if a location is within this boundary using the ray casting algorithm.
     * This is a simplified implementation - in production, PostGIS would handle this.
     */
    public boolean contains(Location location) {
        Objects.requireNonNull(location, "Location cannot be null");
        
        double x = location.getLongitude().doubleValue();
        double y = location.getLatitude().doubleValue();
        
        boolean inside = false;
        int j = vertices.size() - 1;
        
        for (int i = 0; i < vertices.size(); i++) {
            double xi = vertices.get(i).getLongitude().doubleValue();
            double yi = vertices.get(i).getLatitude().doubleValue();
            double xj = vertices.get(j).getLongitude().doubleValue();
            double yj = vertices.get(j).getLatitude().doubleValue();
            
            if (((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
            j = i;
        }
        
        return inside;
    }
    
    /**
     * Check if this boundary intersects with another boundary.
     * Simplified implementation - in production, PostGIS would handle this.
     */
    public boolean intersects(Boundary other) {
        Objects.requireNonNull(other, "Other boundary cannot be null");
        
        // Simple check: if any vertex of one polygon is inside the other
        for (Location vertex : this.vertices) {
            if (other.contains(vertex)) {
                return true;
            }
        }
        
        for (Location vertex : other.vertices) {
            if (this.contains(vertex)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calculate the approximate area of this boundary in square meters.
     * Uses the shoelace formula for polygon area calculation.
     */
    public double getAreaInSquareMeters() {
        if (vertices.size() < 3) {
            return 0.0;
        }
        
        double area = 0.0;
        int n = vertices.size();
        
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            double xi = vertices.get(i).getLongitude().doubleValue();
            double yi = vertices.get(i).getLatitude().doubleValue();
            double xj = vertices.get(j).getLongitude().doubleValue();
            double yj = vertices.get(j).getLatitude().doubleValue();
            
            area += xi * yj - xj * yi;
        }
        
        area = Math.abs(area) / 2.0;
        
        // Convert from degrees squared to square meters (approximate)
        // This is a rough approximation - PostGIS would provide accurate calculations
        double metersPerDegree = 111320; // Approximate meters per degree at equator
        return area * metersPerDegree * metersPerDegree;
    }
    
    /**
     * Get the center point (centroid) of this boundary.
     */
    public Location getCentroid() {
        double sumLat = 0.0;
        double sumLon = 0.0;
        
        for (Location vertex : vertices) {
            sumLat += vertex.getLatitude().doubleValue();
            sumLon += vertex.getLongitude().doubleValue();
        }
        
        double avgLat = sumLat / vertices.size();
        double avgLon = sumLon / vertices.size();
        
        return Location.of(avgLat, avgLon);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boundary boundary = (Boundary) o;
        return Objects.equals(vertices, boundary.vertices);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(vertices);
    }
    
    @Override
    public String toString() {
        return String.format("Boundary{vertices=%d}", vertices.size());
    }
}