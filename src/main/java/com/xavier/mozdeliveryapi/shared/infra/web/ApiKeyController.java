package com.xavier.mozdeliveryapi.shared.infra.web;

import com.xavier.mozdeliveryapi.shared.application.usecase.ApiKeyService;
import com.xavier.mozdeliveryapi.shared.application.usecase.SecurityUtils;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKey;
import com.xavier.mozdeliveryapi.shared.infra.config.RequirePermission;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.Permission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * REST controller for API key management.
 */
@RestController
@RequestMapping("/api/v1/api-keys")
@Tag(name = "API Key Management", description = "Endpoints for managing merchant API keys")
@SecurityRequirement(name = "bearerAuth")
public class ApiKeyController {
    
    private final ApiKeyService apiKeyService;
    
    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new API key", description = "Creates a new API key for the authenticated merchant")
    @ApiResponse(responseCode = "201", description = "API key created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    @RequirePermission(Permission.MANAGE_API_KEYS)
    public ResponseEntity<CreateApiKeyResponse> createApiKey(@Valid @RequestBody CreateApiKeyRequest request) {
        String merchantId = SecurityUtils.getCurrentMerchantId();
        String currentUser = SecurityUtils.getCurrentUserId();
        
        ApiKeyService.CreateApiKeyResult result = apiKeyService.createApiKey(
            merchantId,
            request.name(),
            request.scopes(),
            request.expiresAt(),
            currentUser
        );
        
        CreateApiKeyResponse response = new CreateApiKeyResponse(
            result.apiKey().keyId(),
            result.rawKey(), // Only returned once during creation
            result.apiKey().name(),
            result.apiKey().scopes(),
            result.apiKey().status(),
            result.apiKey().createdAt(),
            result.apiKey().expiresAt()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "List API keys", description = "Lists all API keys for the authenticated merchant")
    @ApiResponse(responseCode = "200", description = "API keys retrieved successfully")
    @RequirePermission(Permission.VIEW_API_KEYS)
    public ResponseEntity<List<ApiKeyResponse>> listApiKeys() {
        String merchantId = SecurityUtils.getCurrentMerchantId();
        
        List<ApiKey> apiKeys = apiKeyService.listMerchantApiKeys(merchantId);
        List<ApiKeyResponse> response = apiKeys.stream()
            .map(this::toApiKeyResponse)
            .toList();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{keyId}")
    @Operation(summary = "Get API key details", description = "Gets details of a specific API key")
    @ApiResponse(responseCode = "200", description = "API key details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "API key not found")
    @RequirePermission(Permission.VIEW_API_KEYS)
    public ResponseEntity<ApiKeyResponse> getApiKey(
            @Parameter(description = "API key ID") @PathVariable String keyId) {
        
        ApiKey apiKey = apiKeyService.getApiKey(keyId);
        
        // Ensure the API key belongs to the current merchant
        String currentMerchantId = SecurityUtils.getCurrentMerchantId();
        if (!apiKey.merchantId().equals(currentMerchantId)) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(toApiKeyResponse(apiKey));
    }
    
    @PutMapping("/{keyId}/revoke")
    @Operation(summary = "Revoke API key", description = "Permanently revokes an API key")
    @ApiResponse(responseCode = "204", description = "API key revoked successfully")
    @ApiResponse(responseCode = "404", description = "API key not found")
    @RequirePermission(Permission.MANAGE_API_KEYS)
    public ResponseEntity<Void> revokeApiKey(
            @Parameter(description = "API key ID") @PathVariable String keyId) {
        
        // Verify ownership before revoking
        ApiKey apiKey = apiKeyService.getApiKey(keyId);
        String currentMerchantId = SecurityUtils.getCurrentMerchantId();
        if (!apiKey.merchantId().equals(currentMerchantId)) {
            return ResponseEntity.notFound().build();
        }
        
        String currentUser = SecurityUtils.getCurrentUserId();
        apiKeyService.revokeApiKey(keyId, currentUser);
        
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{keyId}/suspend")
    @Operation(summary = "Suspend API key", description = "Temporarily suspends an API key")
    @ApiResponse(responseCode = "204", description = "API key suspended successfully")
    @ApiResponse(responseCode = "404", description = "API key not found")
    @RequirePermission(Permission.MANAGE_API_KEYS)
    public ResponseEntity<Void> suspendApiKey(
            @Parameter(description = "API key ID") @PathVariable String keyId) {
        
        // Verify ownership before suspending
        ApiKey apiKey = apiKeyService.getApiKey(keyId);
        String currentMerchantId = SecurityUtils.getCurrentMerchantId();
        if (!apiKey.merchantId().equals(currentMerchantId)) {
            return ResponseEntity.notFound().build();
        }
        
        String currentUser = SecurityUtils.getCurrentUserId();
        apiKeyService.suspendApiKey(keyId, currentUser);
        
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{keyId}/reactivate")
    @Operation(summary = "Reactivate API key", description = "Reactivates a suspended API key")
    @ApiResponse(responseCode = "204", description = "API key reactivated successfully")
    @ApiResponse(responseCode = "404", description = "API key not found")
    @ApiResponse(responseCode = "400", description = "Cannot reactivate revoked API key")
    @RequirePermission(Permission.MANAGE_API_KEYS)
    public ResponseEntity<Void> reactivateApiKey(
            @Parameter(description = "API key ID") @PathVariable String keyId) {
        
        // Verify ownership before reactivating
        ApiKey apiKey = apiKeyService.getApiKey(keyId);
        String currentMerchantId = SecurityUtils.getCurrentMerchantId();
        if (!apiKey.merchantId().equals(currentMerchantId)) {
            return ResponseEntity.notFound().build();
        }
        
        String currentUser = SecurityUtils.getCurrentUserId();
        apiKeyService.reactivateApiKey(keyId, currentUser);
        
        return ResponseEntity.noContent().build();
    }
    
    private ApiKeyResponse toApiKeyResponse(ApiKey apiKey) {
        return new ApiKeyResponse(
            apiKey.keyId(),
            apiKey.name(),
            apiKey.scopes(),
            apiKey.status(),
            apiKey.createdAt(),
            apiKey.expiresAt(),
            apiKey.lastUsedAt()
        );
    }
    
    // Request/Response DTOs
    
    public record CreateApiKeyRequest(
        @NotBlank @Size(min = 1, max = 100) String name,
        @NotEmpty Set<String> scopes,
        Instant expiresAt // Optional - null means no expiration
    ) {}
    
    public record CreateApiKeyResponse(
        String keyId,
        String apiKey, // Only returned during creation
        String name,
        Set<String> scopes,
        com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKeyStatus status,
        Instant createdAt,
        Instant expiresAt
    ) {}
    
    public record ApiKeyResponse(
        String keyId,
        String name,
        Set<String> scopes,
        com.xavier.mozdeliveryapi.shared.domain.valueobject.ApiKeyStatus status,
        Instant createdAt,
        Instant expiresAt,
        Instant lastUsedAt
    ) {}
}