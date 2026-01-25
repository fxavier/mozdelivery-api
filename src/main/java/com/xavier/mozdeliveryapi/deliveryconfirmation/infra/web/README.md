# DCC API Implementation

This package contains the REST API controllers for Delivery Confirmation Code (DCC) operations.

## Controllers

### DeliveryConfirmationController
Primary API for courier apps to interact with DCC system:

- `POST /api/v1/delivery-confirmation/complete` - Complete delivery with DCC validation
- `GET /api/v1/delivery-confirmation/status/{orderId}` - Get DCC status
- `POST /api/v1/delivery-confirmation/resend/{orderId}` - Resend DCC to customer

**Security**: Requires COURIER or ADMIN role

### DCCAdminController
Admin-only API for override operations:

- `POST /api/v1/admin/delivery-confirmation/override` - Perform admin overrides (force expire, force complete)
- `POST /api/v1/admin/delivery-confirmation/clear-lockout` - Clear courier security lockouts
- `GET /api/v1/admin/delivery-confirmation/courier-stats/{courierId}` - Get courier validation statistics

**Security**: Requires ADMIN role only

## Features Implemented

1. **Delivery Completion with DCC Validation**
   - Validates confirmation codes entered by couriers
   - Handles security lockouts and retry limits
   - Provides detailed error messages with remaining attempts

2. **Code Resend Functionality**
   - Allows resending DCC to customers
   - Available to couriers, admins, and merchants

3. **Admin Override Capabilities**
   - Force expire codes
   - Force complete deliveries
   - Clear courier lockouts
   - All operations include comprehensive audit trails

4. **Security Features**
   - Rate limiting per courier
   - Lockout mechanisms for failed attempts
   - Comprehensive validation statistics
   - Role-based access control

## DTOs

- `CompleteDeliveryRequest` - Request for delivery completion
- `DeliveryCompletionResult` - Result of completion attempt
- `DCCStatusResponse` - DCC status information
- `AdminOverrideRequest` - Admin override operations
- `AdminOverrideResult` - Result of admin operations
- `CourierLockoutClearRequest` - Courier lockout clearing

## Validation

All request DTOs include proper validation annotations:
- `@NotNull` for required fields
- `@NotBlank` for string fields that cannot be empty
- Custom validation logic in record constructors

## Error Handling

- Returns appropriate HTTP status codes
- Provides detailed error messages
- Maintains security by not exposing sensitive information
- Includes remaining attempt counts for failed validations