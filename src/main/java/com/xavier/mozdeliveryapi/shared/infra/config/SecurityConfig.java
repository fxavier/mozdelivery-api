package com.xavier.mozdeliveryapi.shared.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Security configuration for OAuth2 resource server with enhanced scope validation.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/orders/**").hasAuthority("SCOPE_order:read")
                .requestMatchers("/api/v1/dispatch/**").hasAuthority("SCOPE_dispatch:read")
                .requestMatchers("/api/v1/tracking/**").hasAuthority("SCOPE_tracking:read")
                .requestMatchers("/api/v1/payments/**").hasAuthority("SCOPE_payment:read")
                .requestMatchers("/api/v1/notifications/**").hasAuthority("SCOPE_notification:read")
                .requestMatchers("/api/v1/compliance/**").hasAuthority("SCOPE_compliance:read")
                .requestMatchers("/api/v1/audit/**").hasAuthority("SCOPE_audit:read")
                .requestMatchers("/api/v1/tenants/**").hasAuthority("SCOPE_tenant:read")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        
        return http.build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>();
            
            // Extract roles
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }
            
            // Extract scopes
            List<String> scopes = jwt.getClaimAsStringList("scope");
            if (scopes != null) {
                for (String scope : scopes) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
                }
            }
            
            // Also check 'scp' claim (alternative scope claim name)
            List<String> scp = jwt.getClaimAsStringList("scp");
            if (scp != null) {
                for (String scope : scp) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
                }
            }
            
            return authorities;
        });
        
        return converter;
    }
}