package com.xavier.mozdeliveryapi.catalog.infra.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.xavier.mozdeliveryapi.catalog.application.dto.CatalogResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.CategoryResponse;
import com.xavier.mozdeliveryapi.catalog.application.dto.ProductResponse;
import com.xavier.mozdeliveryapi.catalog.application.usecase.CatalogApplicationService;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.CatalogStatus;
import com.xavier.mozdeliveryapi.catalog.domain.valueobject.ProductAvailability;
import com.xavier.mozdeliveryapi.merchant.application.dto.MerchantResponse;
import com.xavier.mozdeliveryapi.merchant.application.usecase.MerchantApplicationService;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

/**
 * Unit test for PublicBrowsingController.
 */
@ExtendWith(MockitoExtension.class)
class PublicBrowsingControllerTest {
    
    @Mock
    private MerchantApplicationService merchantService;
    
    @Mock
    private CatalogApplicationService catalogService;
    
    @InjectMocks
    private PublicBrowsingController controller;
    
    private MerchantResponse testMerchant;
    private CatalogResponse testCatalog;
    private CategoryResponse testCategory;
    private ProductResponse testProduct;
    
    @BeforeEach
    void setUp() {
        testMerchant = new MerchantResponse(
            "merchant-1",
            "Test Restaurant",
            "Test Restaurant Display",
            "test@restaurant.com",
            "+258123456789",
            "123 Main St",
            "Maputo",
            "Mozambique",
            Vertical.RESTAURANT,
            MerchantStatus.ACTIVE,
            true,
            true,
            null,
            null
        );
        
        testCatalog = new CatalogResponse(
            "catalog-1",
            "merchant-1",
            "Main Menu",
            "Our main menu",
            List.of("category-1"),
            CatalogStatus.ACTIVE,
            1,
            true,
            1,
            null,
            null
        );
        
        testCategory = new CategoryResponse(
            "category-1",
            "merchant-1",
            "catalog-1",
            "Burgers",
            "Delicious burgers",
            "burger-image.jpg",
            1,
            true,
            null,
            null
        );
        
        testProduct = new ProductResponse(
            "product-1",
            "merchant-1",
            "category-1",
            "Cheeseburger",
            "Classic cheeseburger",
            List.of("burger1.jpg"),
            new BigDecimal("15.99"),
            "USD",
            ProductAvailability.AVAILABLE,
            true,
            true,
            null,
            null,
            null
        );
    }
    
    @Test
    void discoverMerchants_shouldReturnAllPublicMerchants_whenNoFilters() {
        when(merchantService.getAllPublicMerchants()).thenReturn(List.of(testMerchant));
        
        ResponseEntity<List<MerchantResponse>> response = controller.discoverMerchants(null, null);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("merchant-1", response.getBody().get(0).merchantId());
        assertEquals("Test Restaurant", response.getBody().get(0).businessName());
    }
    
    @Test
    void discoverMerchants_shouldReturnMerchantsByCity_whenCityProvided() {
        when(merchantService.getMerchantsByCity("Maputo")).thenReturn(List.of(testMerchant));
        
        ResponseEntity<List<MerchantResponse>> response = controller.discoverMerchants("Maputo", null);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("merchant-1", response.getBody().get(0).merchantId());
    }
    
    @Test
    void discoverMerchants_shouldReturnMerchantsByCityAndVertical_whenBothProvided() {
        when(merchantService.getMerchantsByCityAndVertical("Maputo", Vertical.RESTAURANT))
            .thenReturn(List.of(testMerchant));
        
        ResponseEntity<List<MerchantResponse>> response = controller.discoverMerchants("Maputo", Vertical.RESTAURANT);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("merchant-1", response.getBody().get(0).merchantId());
    }
    
    @Test
    void getMerchant_shouldReturnMerchant_whenExists() {
        when(merchantService.getPublicMerchant("merchant-1")).thenReturn(Optional.of(testMerchant));
        
        ResponseEntity<MerchantResponse> response = controller.getMerchant("merchant-1");
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("merchant-1", response.getBody().merchantId());
        assertEquals("Test Restaurant", response.getBody().businessName());
    }
    
    @Test
    void getMerchant_shouldReturn404_whenNotExists() {
        when(merchantService.getPublicMerchant("nonexistent")).thenReturn(Optional.empty());
        
        ResponseEntity<MerchantResponse> response = controller.getMerchant("nonexistent");
        
        assertEquals(404, response.getStatusCode().value());
    }
    
    @Test
    void getMerchantsByCity_shouldReturnMerchants() {
        when(merchantService.getMerchantsByCity("Maputo")).thenReturn(List.of(testMerchant));
        
        ResponseEntity<List<MerchantResponse>> response = controller.getMerchantsByCity("Maputo");
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("merchant-1", response.getBody().get(0).merchantId());
    }
    
    @Test
    void getMerchantsByCityAndVertical_shouldReturnMerchants() {
        when(merchantService.getMerchantsByCityAndVertical("Maputo", Vertical.RESTAURANT))
            .thenReturn(List.of(testMerchant));
        
        ResponseEntity<List<MerchantResponse>> response = controller.getMerchantsByCityAndVertical("Maputo", Vertical.RESTAURANT);
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("merchant-1", response.getBody().get(0).merchantId());
    }
    
    @Test
    void getMerchantCatalogs_shouldReturnCatalogs_whenMerchantExists() {
        when(merchantService.getPublicMerchant("merchant-1")).thenReturn(Optional.of(testMerchant));
        when(catalogService.getVisibleMerchantCatalogs("merchant-1")).thenReturn(List.of(testCatalog));
        
        ResponseEntity<List<CatalogResponse>> response = controller.getMerchantCatalogs("merchant-1");
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("catalog-1", response.getBody().get(0).id());
    }
    
    @Test
    void getMerchantCatalogs_shouldReturn404_whenMerchantNotExists() {
        when(merchantService.getPublicMerchant("nonexistent")).thenReturn(Optional.empty());
        
        ResponseEntity<List<CatalogResponse>> response = controller.getMerchantCatalogs("nonexistent");
        
        assertEquals(404, response.getStatusCode().value());
    }
    
    @Test
    void getCatalog_shouldReturnCatalog_whenActiveAndExists() {
        when(catalogService.getCatalog("catalog-1")).thenReturn(testCatalog);
        
        ResponseEntity<CatalogResponse> response = controller.getCatalog("catalog-1");
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("catalog-1", response.getBody().id());
        assertEquals("Main Menu", response.getBody().name());
    }
    
    @Test
    void getCatalog_shouldReturn404_whenInactive() {
        CatalogResponse inactiveCatalog = new CatalogResponse(
            "catalog-1", "merchant-1", "Main Menu", "Description", 
            List.of(), CatalogStatus.INACTIVE, 1, false, 0, null, null
        );
        when(catalogService.getCatalog("catalog-1")).thenReturn(inactiveCatalog);
        
        ResponseEntity<CatalogResponse> response = controller.getCatalog("catalog-1");
        
        assertEquals(404, response.getStatusCode().value());
    }
    
    @Test
    void getCatalogCategories_shouldReturnCategories_whenCatalogActive() {
        when(catalogService.getCatalog("catalog-1")).thenReturn(testCatalog);
        when(catalogService.getVisibleCatalogCategories("catalog-1")).thenReturn(List.of(testCategory));
        
        ResponseEntity<List<CategoryResponse>> response = controller.getCatalogCategories("catalog-1");
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("category-1", response.getBody().get(0).id());
    }
    
    @Test
    void getCategory_shouldReturnCategory_whenVisibleAndExists() {
        when(catalogService.getCategory("category-1")).thenReturn(testCategory);
        
        ResponseEntity<CategoryResponse> response = controller.getCategory("category-1");
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("category-1", response.getBody().id());
        assertEquals("Burgers", response.getBody().name());
    }
    
    @Test
    void getCategory_shouldReturn404_whenNotVisible() {
        CategoryResponse hiddenCategory = new CategoryResponse(
            "category-1", "merchant-1", "catalog-1", "Burgers", "Description", 
            "image.jpg", 1, false, null, null
        );
        when(catalogService.getCategory("category-1")).thenReturn(hiddenCategory);
        
        ResponseEntity<CategoryResponse> response = controller.getCategory("category-1");
        
        assertEquals(404, response.getStatusCode().value());
    }
    
    @Test
    void getCategoryProducts_shouldReturnProducts_whenCategoryVisible() {
        when(catalogService.getCategory("category-1")).thenReturn(testCategory);
        when(catalogService.getAvailableCategoryProducts("category-1")).thenReturn(List.of(testProduct));
        
        ResponseEntity<List<ProductResponse>> response = controller.getCategoryProducts("category-1");
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("product-1", response.getBody().get(0).id());
    }
    
    @Test
    void getProduct_shouldReturnProduct_whenAvailableAndExists() {
        when(catalogService.getProduct("product-1")).thenReturn(testProduct);
        
        ResponseEntity<ProductResponse> response = controller.getProduct("product-1");
        
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("product-1", response.getBody().id());
        assertEquals("Cheeseburger", response.getBody().name());
    }
    
    @Test
    void getProduct_shouldReturn404_whenNotAvailable() {
        ProductResponse unavailableProduct = new ProductResponse(
            "product-1", "merchant-1", "category-1", "Cheeseburger", "Description",
            List.of("image.jpg"), new BigDecimal("15.99"), "USD", ProductAvailability.OUT_OF_STOCK,
            true, false, null, null, null
        );
        when(catalogService.getProduct("product-1")).thenReturn(unavailableProduct);
        
        ResponseEntity<ProductResponse> response = controller.getProduct("product-1");
        
        assertEquals(404, response.getStatusCode().value());
    }
}