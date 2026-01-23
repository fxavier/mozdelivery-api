package com.xavier.mozdeliveryapi.order.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

/**
 * Configuration for the Order Management Module.
 */
@Configuration
@ComponentScan(basePackages = "com.xavier.mozdeliveryapi.order")
@ConditionalOnBean(EntityManagerFactory.class)
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.order.infra.persistence")
@EnableTransactionManagement
public class OrderModuleConfiguration {
    // Configuration beans can be added here if needed
}
