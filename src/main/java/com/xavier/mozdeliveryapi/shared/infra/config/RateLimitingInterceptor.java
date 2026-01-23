package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

/**
 * Interceptor for implementing API rate limiting.
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingInterceptor.class);
    
    // Rate limiting configurations
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 100;
    private static final int TENANT_REQUESTS_PER_MINUTE = 1000;
    private static final int USER_REQUESTS_PER_MINUTE = 60;
    private static final int IP_REQUESTS_PER_MINUTE = 30;
    
    private final RateLimitingService rateLimitingService;
    
    public RateLimitingInterceptor(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Skip rate limiting for health checks and public endpoints
        String requestPath = request.getRequestURI();
        if (isPublicEndpoint(requestPath)) {
            return true;
        }
        
        // Apply different rate limiting strategies
        if (!checkTenantRateLimit(request, response)) {
            return false;
        }
        
        if (!checkUserRateLimit(request, response)) {
            return false;
        }
        
        if (!checkIpRateLimit(request, response)) {
            return false;
        }
        
        return true;
    }
    
    private boolean checkTenantRateLimit(HttpServletRequest request, HttpServletResponse response) {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            String tenantKey = "tenant:" + tenantId;
            if (!rateLimitingService.isAllowed(tenantKey, TENANT_REQUESTS_PER_MINUTE)) {
                logger.warn("Rate limit exceeded for tenant: {}", tenantId);
                setRateLimitHeaders(response, tenantKey, TENANT_REQUESTS_PER_MINUTE);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
        }
        return true;
    }
    
    private boolean checkUserRateLimit(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String userId = jwt.getSubject();
            if (userId != null) {
                String userKey = "user:" + userId;
                if (!rateLimitingService.isAllowed(userKey, USER_REQUESTS_PER_MINUTE)) {
                    logger.warn("Rate limit exceeded for user: {}", userId);
                    setRateLimitHeaders(response, userKey, USER_REQUESTS_PER_MINUTE);
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean checkIpRateLimit(HttpServletRequest request, HttpServletResponse response) {
        String clientIp = getClientIpAddress(request);
        String ipKey = "ip:" + clientIp;
        if (!rateLimitingService.isAllowed(ipKey, IP_REQUESTS_PER_MINUTE)) {
            logger.warn("Rate limit exceeded for IP: {}", clientIp);
            setRateLimitHeaders(response, ipKey, IP_REQUESTS_PER_MINUTE);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
        return true;
    }
    
    private void setRateLimitHeaders(HttpServletResponse response, String key, int requestsPerMinute) {
        long remainingTokens = rateLimitingService.getRemainingTokens(key, requestsPerMinute);
        long secondsUntilRefill = rateLimitingService.getSecondsUntilRefill(key, requestsPerMinute);
        
        response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remainingTokens));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + secondsUntilRefill));
        response.setHeader("Retry-After", String.valueOf(secondsUntilRefill));
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private boolean isPublicEndpoint(String requestPath) {
        return requestPath.startsWith("/api/public/") ||
               requestPath.startsWith("/actuator/") ||
               requestPath.startsWith("/swagger-ui/") ||
               requestPath.startsWith("/v3/api-docs") ||
               requestPath.equals("/favicon.ico");
    }
}