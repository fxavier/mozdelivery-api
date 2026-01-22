package com.xavier.mozdeliveryapi.geospatial;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the Geospatial Services Module.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.geospatial.infrastructure")
@EnableTransactionManagement
public class GeospatialModuleConfiguration {
    
    // Configuration beans can be added here as needed
}
