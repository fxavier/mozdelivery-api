package com.xavier.mozdeliveryapi.geospatial.domain;

/**
 * Enumeration of traffic levels that affect delivery route optimization.
 * Each level represents different traffic conditions and their impact on travel time.
 */
public enum TrafficLevel {
    
    /**
     * Light traffic - faster than normal travel times.
     */
    LIGHT("Light traffic - roads are clear"),
    
    /**
     * Normal traffic - standard travel times.
     */
    NORMAL("Normal traffic conditions"),
    
    /**
     * Heavy traffic - slower than normal travel times.
     */
    HEAVY("Heavy traffic - expect delays"),
    
    /**
     * Severe traffic - significantly slower travel times.
     */
    SEVERE("Severe traffic - major delays expected");
    
    private final String description;
    
    TrafficLevel(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the typical speed factor for this traffic level.
     * Values > 1.0 mean faster than normal, < 1.0 mean slower.
     */
    public double getTypicalSpeedFactor() {
        return switch (this) {
            case LIGHT -> 1.2;
            case NORMAL -> 1.0;
            case HEAVY -> 0.6;
            case SEVERE -> 0.3;
        };
    }
    
    /**
     * Check if this traffic level causes delays.
     */
    public boolean causesDelays() {
        return this == HEAVY || this == SEVERE;
    }
    
    /**
     * Check if this traffic level is better than normal.
     */
    public boolean isBetterThanNormal() {
        return this == LIGHT;
    }
    
    /**
     * Get the next worse traffic level, or current if already worst.
     */
    public TrafficLevel getWorse() {
        return switch (this) {
            case LIGHT -> NORMAL;
            case NORMAL -> HEAVY;
            case HEAVY -> SEVERE;
            case SEVERE -> SEVERE;
        };
    }
    
    /**
     * Get the next better traffic level, or current if already best.
     */
    public TrafficLevel getBetter() {
        return switch (this) {
            case SEVERE -> HEAVY;
            case HEAVY -> NORMAL;
            case NORMAL -> LIGHT;
            case LIGHT -> LIGHT;
        };
    }
}