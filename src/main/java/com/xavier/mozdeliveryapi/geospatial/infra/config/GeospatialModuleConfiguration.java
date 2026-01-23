package com.xavier.mozdeliveryapi.geospatial.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

/**
 * Configuration for the Geospatial Services Module.
 */
@Configuration
@ConditionalOnBean(EntityManagerFactory.class)
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.geospatial.infra.persistence")
@EnableTransactionManagement
public class GeospatialModuleConfiguration {
    
    // Configuration beans can be added here as needed
}
