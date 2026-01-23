package com.xavier.mozdeliveryapi.shared.infrastructure.web;

import com.xavier.mozdeliveryapi.geospatial.application.usecase.port.ServiceAreaRepository;
import com.xavier.mozdeliveryapi.order.application.usecase.port.OrderRepository;
import com.xavier.mozdeliveryapi.tenant.application.usecase.port.TenantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test to verify that REST controllers are properly configured and the application context loads.
 */
@SpringBootTest
@ActiveProfiles("test")
class RestControllersTest {
    
    @MockBean
    private ServiceAreaRepository serviceAreaRepository;
    
    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private TenantRepository tenantRepository;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // with all the REST controllers and their dependencies properly configured
    }
}
