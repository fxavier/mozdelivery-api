package com.xavier.mozdeliveryapi.order;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the Order Management Module.
 */
@Configuration
@ComponentScan(basePackages = "com.xavier.mozdeliveryapi.order")
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.order.infrastructure")
@EnableTransactionManagement
public class OrderModuleConfiguration {
    // Configuration beans can be added here if needed
}
