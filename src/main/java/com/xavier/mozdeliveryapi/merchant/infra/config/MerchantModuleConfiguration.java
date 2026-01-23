package com.xavier.mozdeliveryapi.merchant.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the Merchant module.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.merchant.infra.persistence")
@EnableTransactionManagement
public class MerchantModuleConfiguration {
    
    // Configuration beans can be added here as needed
}
