package com.xavier.mozdeliveryapi.shared.infra.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that sets up tenant context for each request.
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);
    
    private final TenantResolver tenantResolver;
    
    public TenantInterceptor(TenantResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = tenantResolver.resolveTenantId(request);
        
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
            logger.debug("Set tenant context: {}", tenantId);
        } else {
            logger.warn("No tenant ID found in request: {} {}", request.getMethod(), request.getRequestURI());
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
        logger.debug("Cleared tenant context");
    }
}