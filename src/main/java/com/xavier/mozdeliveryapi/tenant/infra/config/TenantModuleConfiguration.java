package com.xavier.mozdeliveryapi.tenant.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

/**
 * Configuration for the Tenant Management Module.
 */
@Configuration
@ComponentScan(basePackages = "com.xavier.mozdeliveryapi.tenant")
@ConditionalOnBean(EntityManagerFactory.class)
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.tenant.infra.persistence")
@EnableTransactionManagement
public class TenantModuleConfiguration {
    
    // Configuration beans can be added here as needed
}
