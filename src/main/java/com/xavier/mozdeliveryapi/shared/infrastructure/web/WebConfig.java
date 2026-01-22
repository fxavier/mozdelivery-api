package com.xavier.mozdeliveryapi.shared.infrastructure.web;

import com.xavier.mozdeliveryapi.shared.infrastructure.multitenant.TenantInterceptor;
import com.xavier.mozdeliveryapi.shared.infrastructure.security.ApiVersioningInterceptor;
import com.xavier.mozdeliveryapi.shared.infrastructure.security.RateLimitingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for registering interceptors and other web-related beans.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final TenantInterceptor tenantInterceptor;
    private final RateLimitingInterceptor rateLimitingInterceptor;
    private final ApiVersioningInterceptor apiVersioningInterceptor;
    
    public WebConfig(TenantInterceptor tenantInterceptor,
                     RateLimitingInterceptor rateLimitingInterceptor,
                     ApiVersioningInterceptor apiVersioningInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
        this.rateLimitingInterceptor = rateLimitingInterceptor;
        this.apiVersioningInterceptor = apiVersioningInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // API versioning should be first
        registry.addInterceptor(apiVersioningInterceptor)
            .addPathPatterns("/api/**")
            .order(1);
        
        // Rate limiting should be early in the chain
        registry.addInterceptor(rateLimitingInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/public/**")
            .order(2);
        
        // Tenant resolution should be after security checks
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**", "/actuator/**")
                .order(3);
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .exposedHeaders("X-RateLimit-Limit", "X-RateLimit-Remaining", "X-RateLimit-Reset", 
                           "API-Version", "API-Supported-Versions", "Deprecation", "Sunset", "Warning")
            .maxAge(3600);
    }
}