# Role-Based Authentication System

This document describes the role-based authentication system implemented for the multi-merchant delivery platform.

## Overview

The system implements OAuth2/OIDC-based authentication with role-based access control (RBAC) using JWT tokens. It supports five user roles with specific permissions and access levels.

## User Roles

### ADMIN
- **Description**: Platform administrator with full system access
- **Permissions**: All permissions (can access any resource)
- **Use Cases**: System administration, platform management, support operations

### MERCHANT
- **Description**: Business owner/manager with access to their merchant operations
- **Permissions**: 
  - Merchant management (own merchant only)
  - Catalog and product management
  - Order management (own orders)
  - Analytics (own data)
  - Payment information (own transactions)
- **Use Cases**: Managing business operations, catalog management, order processing

### COURIER
- **Description**: Delivery person with access to delivery operations
- **Permissions**:
  - Delivery operations (assigned deliveries)
  - Order information (assigned orders only)
  - User profile management (own profile)
- **Use Cases**: Delivery completion, route management, status updates

### CLIENT
- **Description**: Registered customer with access to ordering and tracking
- **Permissions**:
  - Browse merchants and products
  - Order management (own orders)
  - Payment processing (own transactions)
  - User profile management (own profile)
- **Use Cases**: Placing orders, tracking deliveries, managing account

### GUEST
- **Description**: Anonymous user with limited access to browsing and guest checkout
- **Permissions**:
  - Public browsing (merchants, catalogs, products)
  - Guest checkout
  - Payment processing (guest orders)
- **Use Cases**: Browsing products, guest checkout without registration

## JWT Token Structure

The system expects JWT tokens with the following claims:

```json
{
  "sub": "user-id",
  "role": "MERCHANT",
  "merchant_id": "uuid-of-merchant",
  "iat": 1234567890,
  "exp": 1234567890
}
```

### Required Claims
- `sub`: User ID (subject)
- `role`: User role (ADMIN, MERCHANT, COURIER, CLIENT, GUEST)

### Optional Claims
- `merchant_id`: Required for MERCHANT role users
- `roles`: Alternative to `role` (array format)
- `scope`/`scp`: Additional scopes for backward compatibility

## Security Annotations

### @RequirePermission
Use this annotation to require specific permissions for method access:

```java
@RequirePermission(Permission.CATALOG_CREATE)
public ResponseEntity<CatalogResponse> createCatalog(@RequestBody CreateCatalogRequest request) {
    // Method implementation
}

@RequirePermission(value = Permission.CATALOG_UPDATE, checkMerchantAccess = true)
public ResponseEntity<CatalogResponse> updateCatalog(@PathVariable String catalogId, @RequestBody UpdateCatalogRequest request) {
    // Method implementation - will check if user can access the merchant
}
```

### @RequireRole
Use this annotation to require specific roles for method access:

```java
@RequireRole(UserRole.ADMIN)
public ResponseEntity<List<UserResponse>> getAllUsers() {
    // Admin-only method
}

@RequireRole({UserRole.ADMIN, UserRole.MERCHANT})
public ResponseEntity<AnalyticsResponse> getAnalytics() {
    // Method accessible by admins and merchants
}
```

## Permission Service

The `PermissionService` provides programmatic access to permission checking:

```java
@Service
public class MyService {
    
    private final PermissionService permissionService;
    
    public void doSomething() {
        // Check permission
        if (permissionService.hasPermission(Permission.CATALOG_READ)) {
            // User has permission
        }
        
        // Check merchant access
        if (permissionService.canAccessMerchant(merchantId)) {
            // User can access this merchant
        }
        
        // Get current user info
        UserRole role = permissionService.getCurrentUserRole();
        String userId = permissionService.getCurrentUserId();
        MerchantId merchantId = permissionService.getCurrentUserMerchantId();
    }
}
```

## Security Configuration

The system is configured in `SecurityConfig.java` with the following endpoint protection:

- **Public endpoints**: `/actuator/health`, `/api/public/**`, Swagger UI
- **Admin endpoints**: `/api/v1/admin/**` (ADMIN only)
- **Merchant endpoints**: `/api/v1/merchants/**`, `/api/v1/catalogs/**` (ADMIN, MERCHANT)
- **Courier endpoints**: `/api/v1/deliveries/**`, `/api/v1/dispatch/**` (ADMIN, COURIER)
- **Client endpoints**: `/api/v1/orders/**`, `/api/v1/tracking/**` (ADMIN, MERCHANT, CLIENT)

## Permission Matrix

| Resource | Admin | Merchant | Courier | Client | Guest |
|----------|-------|----------|---------|--------|-------|
| Browse Merchants | ✓ | ✓ | ✓ | ✓ | ✓ |
| Manage Catalogs | ✓ | ✓ (own) | ✗ | ✗ | ✗ |
| Place Orders | ✓ | ✗ | ✗ | ✓ | ✓ |
| Manage Deliveries | ✓ | ✗ | ✓ (assigned) | ✗ | ✗ |
| View Analytics | ✓ | ✓ (own) | ✗ | ✗ | ✗ |
| Complete Delivery | ✓ | ✗ | ✓ | ✗ | ✗ |
| System Config | ✓ | ✗ | ✗ | ✗ | ✗ |

## Testing

The system includes comprehensive tests:

- `RoleBasedAuthenticationTest`: Tests JWT token processing and role extraction
- `PermissionServiceTest`: Tests permission checking logic
- `SecurityIntegrationTest`: Tests Spring Security configuration

## Usage Examples

### Controller with Role-Based Security
```java
@RestController
@RequestMapping("/api/v1/catalogs")
@RequireRole({UserRole.ADMIN, UserRole.MERCHANT})
public class CatalogController {
    
    @PostMapping
    @RequirePermission(value = Permission.CATALOG_CREATE, checkMerchantAccess = true)
    public ResponseEntity<CatalogResponse> createCatalog(@RequestBody CreateCatalogRequest request) {
        // Implementation
    }
    
    @GetMapping("/{catalogId}")
    @RequirePermission(Permission.CATALOG_READ)
    public ResponseEntity<CatalogResponse> getCatalog(@PathVariable String catalogId) {
        // Implementation
    }
}
```

### Service with Permission Checking
```java
@Service
public class CatalogService {
    
    private final PermissionService permissionService;
    private final SecurityUtils securityUtils;
    
    public CatalogResponse createCatalog(CreateCatalogRequest request) {
        // Ensure user can access the merchant
        securityUtils.ensureMerchantAccess(request.getMerchantId());
        
        // Business logic
        return catalogApplicationService.createCatalog(request);
    }
}
```

## Error Handling

The system provides clear error messages for authorization failures:

- `AccessDeniedException`: Thrown when user lacks required permissions
- HTTP 401: Unauthorized (no valid token)
- HTTP 403: Forbidden (valid token but insufficient permissions)

## Configuration

### JWT Validation
Configure JWT validation in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-oauth-provider.com
          jwk-set-uri: https://your-oauth-provider.com/.well-known/jwks.json
```

### Rate Limiting
The system includes role-based rate limiting configured in `RateLimitingConfig.java`.