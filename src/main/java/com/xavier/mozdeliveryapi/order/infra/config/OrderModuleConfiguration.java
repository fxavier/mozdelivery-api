package com.xavier.mozdeliveryapi.order.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.xavier.mozdeliveryapi.order.application.usecase.port.OrderRepository;
import com.xavier.mozdeliveryapi.order.infra.persistence.JpaOrderRepository;
import com.xavier.mozdeliveryapi.order.infra.persistence.OrderMapper;
import com.xavier.mozdeliveryapi.order.infra.persistence.OrderRepositoryImpl;

/**
 * Configuration for the Order Management Module.
 */
@Configuration
@ComponentScan(basePackages = "com.xavier.mozdeliveryapi.order")
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@EnableJpaRepositories(basePackages = "com.xavier.mozdeliveryapi.order.infra.persistence")
@EntityScan(basePackages = "com.xavier.mozdeliveryapi.order.infra.persistence")
@EnableTransactionManagement
public class OrderModuleConfiguration {
    @Bean
    @ConditionalOnMissingBean(OrderRepository.class)
    OrderRepository orderRepository(JpaOrderRepository jpaRepository, OrderMapper mapper) {
        return new OrderRepositoryImpl(jpaRepository, mapper);
    }
}
