package com.xavier.mozdeliveryapi.tenant.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the Tenant Management Module.
 */
@Configuration
@ComponentScan(basePackages = "com.xavier.mozdeliveryapi.tenant")
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.tenant.infra.persistence")
@EnableTransactionManagement
public class TenantModuleConfiguration {
    
    // Configuration beans can be added here as needed
}
