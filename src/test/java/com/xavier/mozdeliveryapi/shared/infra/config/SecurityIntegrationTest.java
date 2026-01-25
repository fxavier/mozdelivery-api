package com.xavier.mozdeliveryapi.shared.infra.config;

import com.xavier.mozdeliveryapi.shared.domain.valueobject.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for role-based security configuration.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    void shouldAllowPublicEndpointsWithoutAuthentication() throws Exception {
        setUp();
        
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
    
    @Test
    void shouldDenyProtectedEndpointsWithoutAuthentication() throws Exception {
        setUp();
        
        mockMvc.perform(get("/api/v1/catalogs"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccessToAllEndpoints() throws Exception {
        setUp();
        
        mockMvc.perform(get("/api/v1/catalogs"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "MERCHANT")
    void shouldAllowMerchantAccessToCatalogEndpoints() throws Exception {
        setUp();
        
        mockMvc.perform(get("/api/v1/catalogs"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldDenyClientAccessToMerchantEndpoints() throws Exception {
        setUp();
        
        mockMvc.perform(get("/api/v1/catalogs"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "COURIER")
    void shouldDenyCourierAccessToCatalogEndpoints() throws Exception {
        setUp();
        
        mockMvc.perform(get("/api/v1/catalogs"))
                .andExpect(status().isForbidden());
    }
}