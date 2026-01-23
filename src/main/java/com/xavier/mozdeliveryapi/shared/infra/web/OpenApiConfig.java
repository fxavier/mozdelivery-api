package com.xavier.mozdeliveryapi.shared.infra.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import com.xavier.mozdeliveryapi.dispatch.domain.entity.Delivery;

/**
 * OpenAPI/Swagger configuration for the delivery platform API.
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI deliveryPlatformOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Mozambique Delivery Platform API")
                .description("Multi-tenant, multi-city, multi-vertical delivery platform for Mozambique market")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("Platform Team")
                    .email("platform@mozdelivery.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://mozdelivery.com/license")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development server"),
                new Server()
                    .url("https://api-staging.mozdelivery.com")
                    .description("Staging server"),
                new Server()
                    .url("https://api.mozdelivery.com")
                    .description("Production server")))
            .addSecurityItem(new SecurityRequirement()
                .addList("OAuth2"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("OAuth2", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .description("OAuth2 authentication with scopes")
                    .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                        .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                            .authorizationUrl("https://auth.mozdelivery.com/oauth2/authorize")
                            .tokenUrl("https://auth.mozdelivery.com/oauth2/token")
                            .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                .addString("order:read", "Read orders")
                                .addString("order:write", "Create and modify orders")
                                .addString("dispatch:read", "Read dispatch information")
                                .addString("dispatch:write", "Create and modify dispatch assignments")
                                .addString("tracking:read", "Read tracking information")
                                .addString("tracking:write", "Update tracking information")
                                .addString("payment:read", "Read payment information")
                                .addString("payment:write", "Process payments and refunds")
                                .addString("notification:read", "Read notifications")
                                .addString("notification:write", "Send notifications")
                                .addString("notification:admin", "Administrative notification operations")
                                .addString("compliance:read", "Read compliance information")
                                .addString("compliance:write", "Manage compliance data")
                                .addString("audit:read", "Read audit logs")
                                .addString("audit:admin", "Administrative audit operations")
                                .addString("tenant:read", "Read tenant information")
                                .addString("tenant:write", "Manage tenant data"))))));
    }
}