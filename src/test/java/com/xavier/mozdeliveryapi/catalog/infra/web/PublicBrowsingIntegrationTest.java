package com.xavier.mozdeliveryapi.catalog.infra.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import com.xavier.mozdeliveryapi.catalog.application.usecase.port.CatalogRepository;
import com.xavier.mozdeliveryapi.catalog.application.usecase.port.CategoryRepository;
import com.xavier.mozdeliveryapi.catalog.application.usecase.port.ProductRepository;
import com.xavier.mozdeliveryapi.geospatial.application.usecase.port.ServiceAreaRepository;
import com.xavier.mozdeliveryapi.merchant.application.usecase.port.MerchantRepository;
import com.xavier.mozdeliveryapi.order.application.usecase.port.OrderRepository;
import com.xavier.mozdeliveryapi.tenant.application.usecase.port.TenantRepository;

/**
 * Integration test to verify that PublicBrowsingController loads correctly in the Spring context.
 */
@SpringBootTest
@ActiveProfiles("test")
class PublicBrowsingIntegrationTest {
    
    @MockBean
    private ServiceAreaRepository serviceAreaRepository;
    
    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private TenantRepository tenantRepository;
    
    @MockBean
    private MerchantRepository merchantRepository;
    
    @MockBean
    private CatalogRepository catalogRepository;
    
    @MockBean
    private CategoryRepository categoryRepository;
    
    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // with the PublicBrowsingController and its dependencies properly configured
    }
}