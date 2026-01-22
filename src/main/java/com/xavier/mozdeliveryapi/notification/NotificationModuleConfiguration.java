package com.xavier.mozdeliveryapi.notification;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the Notification Services Module.
 */
@Configuration
@ComponentScan(basePackages = "com.xavier.mozdeliveryapi.notification")
@EnableTransactionManagement
public class NotificationModuleConfiguration {
    // Configuration beans can be added here if needed
}