package com.xavier.mozdeliveryapi.shared.infrastructure.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test to verify that REST controllers are properly configured and the application context loads.
 */
@SpringBootTest
@ActiveProfiles("test")
class RestControllersTest {
    
    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // with all the REST controllers and their dependencies properly configured
    }
}