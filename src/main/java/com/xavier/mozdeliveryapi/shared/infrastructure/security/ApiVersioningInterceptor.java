package com.xavier.mozdeliveryapi.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * Interceptor for API versioning and backward compatibility.
 */
@Component
public class ApiVersioningInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiVersioningInterceptor.class);
    
    private static final String CURRENT_VERSION = "v1";
    private static final Set<String> SUPPORTED_VERSIONS = Set.of("v1");
    private static final Set<String> DEPRECATED_VERSIONS = Set.of(); // Add deprecated versions here
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String requestPath = request.getRequestURI();
        
        // Skip versioning for public endpoints
        if (isPublicEndpoint(requestPath)) {
            return true;
        }
        
        // Extract version from URL path
        String version = extractVersionFromPath(requestPath);
        
        if (version == null) {
            // No version specified, assume current version
            version = CURRENT_VERSION;
            response.setHeader("API-Version", CURRENT_VERSION);
            return true;
        }
        
        // Check if version is supported
        if (!SUPPORTED_VERSIONS.contains(version)) {
            logger.warn("Unsupported API version requested: {} from IP: {}", version, request.getRemoteAddr());
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(String.format(
                "{\"error\":\"UNSUPPORTED_VERSION\",\"message\":\"API version %s is not supported. Supported versions: %s\",\"supportedVersions\":%s}",
                version, SUPPORTED_VERSIONS, SUPPORTED_VERSIONS.toString()));
            return false;
        }
        
        // Check if version is deprecated
        if (DEPRECATED_VERSIONS.contains(version)) {
            logger.info("Deprecated API version used: {} from IP: {}", version, request.getRemoteAddr());
            response.setHeader("Deprecation", "true");
            response.setHeader("Sunset", "2024-12-31T23:59:59Z"); // Example sunset date
            response.setHeader("Link", "</api/v" + CURRENT_VERSION + ">; rel=\"successor-version\"");
            response.setHeader("Warning", "299 - \"This API version is deprecated. Please migrate to " + CURRENT_VERSION + "\"");
        }
        
        // Set version headers
        response.setHeader("API-Version", version);
        response.setHeader("API-Supported-Versions", String.join(",", SUPPORTED_VERSIONS));
        
        return true;
    }
    
    private String extractVersionFromPath(String requestPath) {
        // Extract version from path like /api/v1/orders -> v1
        if (requestPath.startsWith("/api/")) {
            String[] pathParts = requestPath.split("/");
            if (pathParts.length >= 3 && pathParts[2].startsWith("v")) {
                return pathParts[2];
            }
        }
        return null;
    }
    
    private boolean isPublicEndpoint(String requestPath) {
        return requestPath.startsWith("/api/public/") ||
               requestPath.startsWith("/actuator/") ||
               requestPath.startsWith("/swagger-ui/") ||
               requestPath.startsWith("/v3/api-docs") ||
               requestPath.equals("/favicon.ico");
    }
    
    /**
     * Add a new supported version.
     * 
     * @param version the version to add
     */
    public static void addSupportedVersion(String version) {
        // In a real implementation, this would be configurable
        logger.info("Adding supported API version: {}", version);
    }
    
    /**
     * Mark a version as deprecated.
     * 
     * @param version the version to deprecate
     */
    public static void deprecateVersion(String version) {
        // In a real implementation, this would be configurable
        logger.info("Deprecating API version: {}", version);
    }
}