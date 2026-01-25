package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.application.usecase.ApiKeyService;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKey;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Filter for API key authentication.
 */
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String API_KEY_PREFIX = "ApiKey ";
    
    private final ApiKeyService apiKeyService;
    
    public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // Skip if already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String apiKey = extractApiKey(request);
        if (apiKey != null) {
            authenticateWithApiKey(request, apiKey);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractApiKey(HttpServletRequest request) {
        // Check X-API-Key header first
        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return apiKey.trim();
        }
        
        // Check Authorization header with ApiKey prefix
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(API_KEY_PREFIX)) {
            return authHeader.substring(API_KEY_PREFIX.length()).trim();
        }
        
        return null;
    }
    
    private void authenticateWithApiKey(HttpServletRequest request, String rawApiKey) {
        try {
            ApiKeyService.ApiKeyValidationResult result = apiKeyService.validateApiKey(rawApiKey);
            
            if (result.valid()) {
                ApiKey apiKey = result.apiKey();
                
                // Create authentication token with merchant role and scopes
                List<SimpleGrantedAuthority> authorities = createAuthorities(apiKey);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        apiKey.merchantId(), 
                        null, 
                        authorities
                    );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Add API key information to authentication details
                ApiKeyAuthenticationDetails details = new ApiKeyAuthenticationDetails(
                    apiKey.keyId(),
                    apiKey.merchantId(),
                    apiKey.scopes(),
                    request
                );
                authentication.setDetails(details);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug("Authenticated request with API key {} for merchant {}", 
                           apiKey.keyId(), apiKey.merchantId());
            } else {
                logger.warn("Invalid API key authentication attempt: {}", result.reason());
            }
            
        } catch (Exception e) {
            logger.error("Error during API key authentication", e);
        }
    }
    
    private List<SimpleGrantedAuthority> createAuthorities(ApiKey apiKey) {
        // API keys always get MERCHANT role
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + UserRole.MERCHANT.name())
        );
        
        // Add scope-based authorities
        apiKey.scopes().forEach(scope -> 
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope))
        );
        
        return authorities;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip for public endpoints
        return path.startsWith("/api/public/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/favicon.ico");
    }
    
    /**
     * Custom authentication details for API key authentication.
     */
    public static class ApiKeyAuthenticationDetails extends WebAuthenticationDetailsSource {
        private final String keyId;
        private final String merchantId;
        private final Set<String> scopes;
        private final String remoteAddress;
        private final String sessionId;
        
        public ApiKeyAuthenticationDetails(String keyId, String merchantId, Set<String> scopes, 
                                         HttpServletRequest request) {
            this.keyId = keyId;
            this.merchantId = merchantId;
            this.scopes = scopes;
            this.remoteAddress = request.getRemoteAddr();
            this.sessionId = request.getSession(false) != null ? request.getSession().getId() : null;
        }
        
        public String getKeyId() { return keyId; }
        public String getMerchantId() { return merchantId; }
        public Set<String> getScopes() { return scopes; }
        public String getRemoteAddress() { return remoteAddress; }
        public String getSessionId() { return sessionId; }
        
        public boolean hasScope(String scope) {
            return scopes.contains(scope);
        }
    }
}