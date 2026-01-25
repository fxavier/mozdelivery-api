package com.xavier.mozdeliveryapi.shared.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Enhanced security configuration for OAuth2 resource server with role-based authentication and API key support.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private final RoleBasedJwtAuthenticationConverter jwtAuthenticationConverter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    
    public SecurityConfig(RoleBasedJwtAuthenticationConverter jwtAuthenticationConverter,
                         ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Admin endpoints - admin role required
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                
                // Merchant endpoints - admin or merchant role required
                .requestMatchers("/api/v1/merchants/**").hasAnyRole("ADMIN", "MERCHANT")
                .requestMatchers("/api/v1/catalogs/**").hasAnyRole("ADMIN", "MERCHANT")
                .requestMatchers("/api/v1/categories/**").hasAnyRole("ADMIN", "MERCHANT")
                .requestMatchers("/api/v1/products/**").hasAnyRole("ADMIN", "MERCHANT")
                
                // API key management endpoints - admin or merchant role required
                .requestMatchers("/api/v1/api-keys/**").hasAnyRole("ADMIN", "MERCHANT")
                
                // Courier endpoints - admin or courier role required
                .requestMatchers("/api/v1/deliveries/**").hasAnyRole("ADMIN", "COURIER")
                .requestMatchers("/api/v1/dispatch/**").hasAnyRole("ADMIN", "COURIER")
                .requestMatchers("/api/v1/delivery-confirmation/**").hasAnyRole("ADMIN", "COURIER")
                
                // Client endpoints - admin, merchant, or client role required
                .requestMatchers("/api/v1/orders/**").hasAnyRole("ADMIN", "MERCHANT", "CLIENT")
                .requestMatchers("/api/v1/tracking/**").hasAnyRole("ADMIN", "COURIER", "CLIENT")
                
                // Payment endpoints - authenticated users only
                .requestMatchers("/api/v1/payments/**").hasAnyRole("ADMIN", "MERCHANT", "CLIENT")
                
                // Notification endpoints - authenticated users only
                .requestMatchers("/api/v1/notifications/**").hasAnyRole("ADMIN", "MERCHANT", "COURIER", "CLIENT")
                
                // Compliance and audit endpoints - admin and merchant only
                .requestMatchers("/api/v1/compliance/**").hasAnyRole("ADMIN", "MERCHANT")
                .requestMatchers("/api/v1/audit/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Add API key authentication filter before JWT processing
            .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
            );
        
        return http.build();
    }
}