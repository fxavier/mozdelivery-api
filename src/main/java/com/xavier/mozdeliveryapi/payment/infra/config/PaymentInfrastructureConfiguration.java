package com.xavier.mozdeliveryapi.payment.infra.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for payment infrastructure components.
 */
@Configuration
public class PaymentInfrastructureConfiguration {
    
    /**
     * RestTemplate for payment gateway API calls.
     */
    @Bean("paymentRestTemplate")
    public RestTemplate paymentRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Configure timeouts for payment operations
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("User-Agent", "MozDeliveryAPI/1.0");
            request.getHeaders().add("Accept", "application/json");
            request.getHeaders().add("Content-Type", "application/json");
            return execution.execute(request, body);
        });
        
        return restTemplate;
    }
    
    /**
     * Configuration properties for M-Pesa integration.
     */
    @Bean
    @ConfigurationProperties(prefix = "payment.mpesa")
    public MPesaProperties mpesaProperties() {
        return new MPesaProperties();
    }
    
    /**
     * Configuration properties for Multibanco integration.
     */
    @Bean
    @ConfigurationProperties(prefix = "payment.multibanco")
    public MultibancoProperties multibancoProperties() {
        return new MultibancoProperties();
    }
    
    /**
     * Configuration properties for card payment integration.
     */
    @Bean
    @ConfigurationProperties(prefix = "payment.cards")
    public CardPaymentProperties cardPaymentProperties() {
        return new CardPaymentProperties();
    }
    
    /**
     * M-Pesa configuration properties.
     */
    public static class MPesaProperties {
        private String apiUrl;
        private String consumerKey;
        private String consumerSecret;
        private String shortCode;
        private String passkey;
        private Duration timeout = Duration.ofSeconds(30);
        
        // Getters and setters
        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        
        public String getConsumerKey() { return consumerKey; }
        public void setConsumerKey(String consumerKey) { this.consumerKey = consumerKey; }
        
        public String getConsumerSecret() { return consumerSecret; }
        public void setConsumerSecret(String consumerSecret) { this.consumerSecret = consumerSecret; }
        
        public String getShortCode() { return shortCode; }
        public void setShortCode(String shortCode) { this.shortCode = shortCode; }
        
        public String getPasskey() { return passkey; }
        public void setPasskey(String passkey) { this.passkey = passkey; }
        
        public Duration getTimeout() { return timeout; }
        public void setTimeout(Duration timeout) { this.timeout = timeout; }
    }
    
    /**
     * Multibanco configuration properties.
     */
    public static class MultibancoProperties {
        private String apiUrl;
        private String apiKey;
        private String entity;
        private Duration timeout = Duration.ofSeconds(30);
        
        // Getters and setters
        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        
        public String getEntity() { return entity; }
        public void setEntity(String entity) { this.entity = entity; }
        
        public Duration getTimeout() { return timeout; }
        public void setTimeout(Duration timeout) { this.timeout = timeout; }
    }
    
    /**
     * Card payment configuration properties.
     */
    public static class CardPaymentProperties {
        private String apiUrl;
        private String merchantId;
        private String apiKey;
        private String encryptionKey;
        private Duration timeout = Duration.ofSeconds(45);
        private boolean require3DSecure = true;
        
        // Getters and setters
        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        
        public String getMerchantId() { return merchantId; }
        public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
        
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        
        public String getEncryptionKey() { return encryptionKey; }
        public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }
        
        public Duration getTimeout() { return timeout; }
        public void setTimeout(Duration timeout) { this.timeout = timeout; }
        
        public boolean isRequire3DSecure() { return require3DSecure; }
        public void setRequire3DSecure(boolean require3DSecure) { this.require3DSecure = require3DSecure; }
    }
}