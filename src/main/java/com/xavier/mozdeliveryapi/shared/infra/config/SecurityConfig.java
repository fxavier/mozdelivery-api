package com.xavier.mozdeliveryapi.shared.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for OAuth2 resource server with role-based authentication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private final RoleBasedJwtAuthenticationConverter jwtAuthenticationConverter;
    
    public SecurityConfig(RoleBasedJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                
                // Merchant endpoints
                .requestMatchers("/api/v1/merchants/**").hasAnyRole("ADMIN", "MERCHANT")
                .requestMatchers("/api/v1/catalogs/**").hasAnyRole("ADMIN", "MERCHANT")
                .requestMatchers("/api/v1/categories/**").hasAnyRole("ADMIN", "MERCHANT")
                .requestMatchers("/api/v1/products/**").hasAnyRole("ADMIN", "MERCHANT")
                
                // Courier endpoints
                .requestMatchers("/api/v1/deliveries/**").hasAnyRole("ADMIN", "COURIER")
                .requestMatchers("/api/v1/dispatch/**").hasAnyRole("ADMIN", "COURIER")
                
                // Client endpoints
                .requestMatchers("/api/v1/orders/**").hasAnyRole("ADMIN", "MERCHANT", "CLIENT")
                .requestMatchers("/api/v1/tracking/**").hasAnyRole("ADMIN", "COURIER", "CLIENT")
                
                // Payment endpoints (authenticated users)
                .requestMatchers("/api/v1/payments/**").hasAnyRole("ADMIN", "MERCHANT", "CLIENT")
                
                // Notification endpoints
                .requestMatchers("/api/v1/notifications/**").hasAnyRole("ADMIN", "MERCHANT", "COURIER", "CLIENT")
                
                // Compliance and audit (admin and merchant)
                .requestMatchers("/api/v1/compliance/**").hasAnyRole("ADMIN", "MERCHANT")
                .requestMatchers("/api/v1/audit/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)
                )
            );
        
        return http.build();
    }
}