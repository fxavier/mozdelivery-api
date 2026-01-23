package com.xavier.mozdeliveryapi.geospatial.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the Geospatial Services Module.
 */
@Configuration
@ComponentScan(basePackages = "com.xavier.mozdeliveryapi.geospatial")
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.geospatial.infra.persistence")
@EnableTransactionManagement
public class GeospatialModuleConfiguration {
    
    // Configuration beans can be added here as needed
}
