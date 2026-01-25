package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced interceptor for implementing role-based and endpoint-specific API rate limiting.
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingInterceptor.class);
    
    // Role-based rate limiting configurations (requests per minute)
    private static final Map<UserRole, Integer> ROLE_RATE_LIMITS = Map.of(
        UserRole.ADMIN, 5000,
        UserRole.MERCHANT, 1000,
        UserRole.COURIER, 500,
        UserRole.CLIENT, 100
    );
    
    // Endpoint-specific rate limiting (requests per minute)
    private static final Map<String, Integer> ENDPOINT_RATE_LIMITS = new ConcurrentHashMap<>();
    
    static {
        // Guest checkout endpoints (more restrictive)
        ENDPOINT_RATE_LIMITS.put("/api/public/orders/guest", 10);
        ENDPOINT_RATE_LIMITS.put("/api/public/orders/guest/track", 30);
        ENDPOINT_RATE_LIMITS.put("/api/public/orders/guest/resend-code", 5);
        
        // DCC validation endpoints (very restrictive)
        ENDPOINT_RATE_LIMITS.put("/api/v1/deliveries/*/complete", 20);
        ENDPOINT_RATE_LIMITS.put("/api/v1/delivery-confirmation/*/validate", 20);
        
        // Public browsing endpoints
        ENDPOINT_RATE_LIMITS.put("/api/public/merchants", 200);
        ENDPOINT_RATE_LIMITS.put("/api/public/catalogs", 200);
        ENDPOINT_RATE_LIMITS.put("/api/public/products", 200);
        
        // Payment endpoints (more restrictive)
        ENDPOINT_RATE_LIMITS.put("/api/v1/payments", 50);
        
        // Admin endpoints (less restrictive for admins)
        ENDPOINT_RATE_LIMITS.put("/api/v1/admin/**", 2000);
    }
    
    // Default rate limits
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 100;
    private static final int TENANT_REQUESTS_PER_MINUTE = 1000;
    private static final int IP_REQUESTS_PER_MINUTE = 30;
    private static final int API_KEY_REQUESTS_PER_MINUTE = 2000;
    
    private final RateLimitingService rateLimitingService;
    
    public RateLimitingInterceptor(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Skip rate limiting for health checks and some public endpoints
        String requestPath = request.getRequestURI();
        if (isExemptEndpoint(requestPath)) {
            return true;
        }
        
        // Apply different rate limiting strategies in order of priority
        
        // 1. Endpoint-specific rate limiting (highest priority)
        if (!checkEndpointRateLimit(request, response)) {
            return false;
        }
        
        // 2. Role-based rate limiting
        if (!checkRoleBasedRateLimit(request, response)) {
            return false;
        }
        
        // 3. API key rate limiting (for API key authentication)
        if (!checkApiKeyRateLimit(request, response)) {
            return false;
        }
        
        // 4. Tenant-based rate limiting
        if (!checkTenantRateLimit(request, response)) {
            return false;
        }
        
        // 5. User-based rate limiting
        if (!checkUserRateLimit(request, response)) {
            return false;
        }
        
        // 6. IP-based rate limiting (lowest priority, fallback)
        if (!checkIpRateLimit(request, response)) {
            return false;
        }
        
        return true;
    }
    
    private boolean checkEndpointRateLimit(HttpServletRequest request, HttpServletResponse response) {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        String endpointKey = method + " " + requestPath;
        
        // Check exact match first
        Integer limit = ENDPOINT_RATE_LIMITS.get(endpointKey);
        if (limit == null) {
            // Check path patterns
            limit = ENDPOINT_RATE_LIMITS.entrySet().stream()
                .filter(entry -> pathMatches(requestPath, entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        }
        
        if (limit != null) {
            String rateLimitKey = "endpoint:" + endpointKey;
            if (!rateLimitingService.isAllowed(rateLimitKey, limit)) {
                logger.warn("Endpoint rate limit exceeded for {}: {}", endpointKey, requestPath);
                setRateLimitHeaders(response, rateLimitKey, limit);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
        }
        
        return true;
    }
    
    private boolean checkRoleBasedRateLimit(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserRole userRole = extractUserRole(authentication);
            if (userRole != null) {
                Integer limit = ROLE_RATE_LIMITS.get(userRole);
                if (limit != null) {
                    String roleKey = "role:" + userRole.name() + ":" + getUserId(authentication);
                    if (!rateLimitingService.isAllowed(roleKey, limit)) {
                        logger.warn("Role-based rate limit exceeded for role {}: {}", userRole, getUserId(authentication));
                        setRateLimitHeaders(response, roleKey, limit);
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean checkApiKeyRateLimit(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            // This indicates API key authentication
            Object details = authentication.getDetails();
            if (details instanceof ApiKeyAuthenticationFilter.ApiKeyAuthenticationDetails apiKeyDetails) {
                String apiKeyId = apiKeyDetails.getKeyId();
                String rateLimitKey = "apikey:" + apiKeyId;
                
                if (!rateLimitingService.isAllowed(rateLimitKey, API_KEY_REQUESTS_PER_MINUTE)) {
                    logger.warn("API key rate limit exceeded for key: {}", apiKeyId);
                    setRateLimitHeaders(response, rateLimitKey, API_KEY_REQUESTS_PER_MINUTE);
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    return false;
                }
            }
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
                // Use role-specific rate limit if available, otherwise use default
                UserRole userRole = extractUserRole(authentication);
                int limit = userRole != null ? ROLE_RATE_LIMITS.getOrDefault(userRole, DEFAULT_REQUESTS_PER_MINUTE) : DEFAULT_REQUESTS_PER_MINUTE;
                
                String userKey = "user:" + userId;
                if (!rateLimitingService.isAllowed(userKey, limit)) {
                    logger.warn("Rate limit exceeded for user: {}", userId);
                    setRateLimitHeaders(response, userKey, limit);
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
    
    private UserRole extractUserRole(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> authority.startsWith("ROLE_"))
            .map(authority -> authority.substring(5)) // Remove "ROLE_" prefix
            .map(roleName -> {
                try {
                    return UserRole.valueOf(roleName);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            })
            .filter(role -> role != null)
            .findFirst()
            .orElse(null);
    }
    
    private String getUserId(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getSubject();
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return authentication.getName();
        }
        return "unknown";
    }
    
    private boolean pathMatches(String requestPath, String pattern) {
        if (pattern.contains("*")) {
            // Simple wildcard matching
            String regex = pattern.replace("*", ".*");
            return requestPath.matches(regex);
        }
        return requestPath.equals(pattern);
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
    
    private boolean isExemptEndpoint(String requestPath) {
        return requestPath.startsWith("/actuator/health") ||
               requestPath.startsWith("/actuator/info") ||
               requestPath.startsWith("/swagger-ui/") ||
               requestPath.startsWith("/v3/api-docs") ||
               requestPath.equals("/favicon.ico");
    }
}