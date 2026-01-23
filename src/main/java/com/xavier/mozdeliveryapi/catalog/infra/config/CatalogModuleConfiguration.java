package com.xavier.mozdeliveryapi.catalog.infra.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration for the Catalog module.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.catalog.infra.persistence")
@EntityScan(basePackages = "com.xavier.mozdeliveryapi.catalog.infra.persistence")
public class CatalogModuleConfiguration {
    
    // Module configuration for catalog domain
    // Enables JPA repositories and entity scanning for the catalog module
}