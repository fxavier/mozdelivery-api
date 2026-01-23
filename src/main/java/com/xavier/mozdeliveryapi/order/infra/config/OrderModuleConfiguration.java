package com.xavier.mozdeliveryapi.order.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the Order Management Module.
 */
@Configuration
@ComponentScan(basePackages = "com.xavier.mozdeliveryapi.order")
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.order.infra.persistence")
@EnableTransactionManagement
public class OrderModuleConfiguration {
    // Configuration beans can be added here if needed
}
