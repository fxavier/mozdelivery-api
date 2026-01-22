package com.xavier.mozdeliveryapi.shared.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for rate limiting.
 */
@Configuration
@ConfigurationProperties(prefix = "app.rate-limiting")
public class RateLimitingConfig {
    
    private int defaultRequestsPerMinute = 100;
    private int tenantRequestsPerMinute = 1000;
    private int userRequestsPerMinute = 60;
    private int ipRequestsPerMinute = 30;
    private int adminRequestsPerMinute = 5000;
    
    private boolean enabled = true;
    private boolean logViolations = true;
    
    // Getters and setters
    public int getDefaultRequestsPerMinute() {
        return defaultRequestsPerMinute;
    }
    
    public void setDefaultRequestsPerMinute(int defaultRequestsPerMinute) {
        this.defaultRequestsPerMinute = defaultRequestsPerMinute;
    }
    
    public int getTenantRequestsPerMinute() {
        return tenantRequestsPerMinute;
    }
    
    public void setTenantRequestsPerMinute(int tenantRequestsPerMinute) {
        this.tenantRequestsPerMinute = tenantRequestsPerMinute;
    }
    
    public int getUserRequestsPerMinute() {
        return userRequestsPerMinute;
    }
    
    public void setUserRequestsPerMinute(int userRequestsPerMinute) {
        this.userRequestsPerMinute = userRequestsPerMinute;
    }
    
    public int getIpRequestsPerMinute() {
        return ipRequestsPerMinute;
    }
    
    public void setIpRequestsPerMinute(int ipRequestsPerMinute) {
        this.ipRequestsPerMinute = ipRequestsPerMinute;
    }
    
    public int getAdminRequestsPerMinute() {
        return adminRequestsPerMinute;
    }
    
    public void setAdminRequestsPerMinute(int adminRequestsPerMinute) {
        this.adminRequestsPerMinute = adminRequestsPerMinute;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isLogViolations() {
        return logViolations;
    }
    
    public void setLogViolations(boolean logViolations) {
        this.logViolations = logViolations;
    }
}