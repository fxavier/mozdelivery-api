# Requirements Document

## Introduction

This document specifies the requirements for a multi-merchant delivery marketplace targeting the Mozambique market. The platform enables merchants across various verticals (restaurants, grocery, pharmacies, convenience stores, electronics, florists, beverages, fuel station convenience) to sell products through mobile apps and web interfaces, with support for guest checkout and secure delivery confirmation.

## Glossary

- **Platform**: The complete delivery marketplace including mobile apps, web backoffice, and backend services
- **Merchant**: A business entity (restaurant, pharmacy, etc.) selling products on the platform (technical term: Tenant)
- **Tenant**: Technical term for merchant in the backend system
- **Client**: End customer placing orders (registered or guest)
- **Courier**: Delivery person fulfilling orders
- **Vertical**: A business category (restaurant, grocery, pharmacy, etc.)
- **Guest Checkout**: Ordering without creating an account, using address + contact details
- **Delivery Confirmation Code (DCC)**: A short unique OTP/PIN issued after order confirmation and required to confirm delivery completion
- **Order_Management_System**: The core system handling order lifecycle from creation to completion
- **Payment_Gateway**: The system processing payments through various local and international methods
- **Dispatch_System**: The system managing delivery assignment and routing
- **Tracking_Service**: The real-time location and status tracking system
- **Notification_Service**: The system sending real-time updates to users and businesses
- **Analytics_Engine**: The system generating business intelligence and reporting
- **Compliance_Module**: The system ensuring regulatory compliance (GDPR, pharmacy regulations)
- **Audit_System**: The system maintaining comprehensive audit trails
- **Geospatial_Service**: The system handling location-based operations and routing

## Requirements

### Requirement 1: Multi-Tenant Architecture

**User Story:** As a platform operator, I want to support multiple business tenants with complete data isolation, so that each business can operate independently while sharing platform infrastructure.

#### Acceptance Criteria

1. WHEN a tenant is onboarded, THE Platform SHALL create isolated data spaces with no cross-tenant data access
2. WHEN processing tenant requests, THE Platform SHALL enforce tenant-specific configurations and business rules
3. WHEN a tenant accesses the system, THE Platform SHALL authenticate and authorize access only to their own data and resources
4. WHEN system failures occur, THE Platform SHALL isolate failures to prevent cross-tenant impact
5. WHERE tenant-specific customizations are required, THE Platform SHALL support per-tenant configuration without affecting other tenants

### Requirement 2: Multi-City Geospatial Operations

**User Story:** As a platform operator, I want to support delivery operations across multiple cities, so that the platform can scale geographically while maintaining efficient routing and service areas.

#### Acceptance Criteria

1. WHEN defining service areas, THE Geospatial_Service SHALL support city-specific delivery zones with precise geographic boundaries
2. WHEN calculating delivery routes, THE Geospatial_Service SHALL optimize routes within city boundaries and account for local traffic patterns
3. WHEN users search for businesses, THE Platform SHALL return results filtered by their current city and delivery availability
4. WHEN processing orders, THE Order_Management_System SHALL validate delivery addresses against city-specific service areas
5. WHEN managing delivery capacity, THE Dispatch_System SHALL allocate resources per city with independent scaling

### Requirement 3: Multi-Vertical Business Support (Merchant-focused)

**User Story:** As a merchant, I want to register my business under a vertical (restaurant, pharmacy, grocery, etc.), so that I can manage my store and sell products with vertical-specific rules.

#### Acceptance Criteria

1. WHEN a business registers, THE Platform SHALL create a Merchant (Tenant) and assign it to a vertical with appropriate feature flags
2. WHEN a user browses the platform, THE Platform SHALL list all Merchants available in the user's city and show their vertical type (grocery/pharmacy/restaurant/etc.)
3. WHEN a user selects a Merchant, THE Platform SHALL display that Merchant's catalogs, categories, and products
4. WHERE pharmacy operations are involved, THE Platform SHALL enforce prescription validation and age verification requirements that are not applied to other verticals
5. THE Platform SHALL support merchant onboarding approval (manual or automated) before the merchant becomes publicly visible

### Requirement 3A: Merchant Catalog, Category, and Product Management

**User Story:** As a merchant, I want to manage catalogs, categories, and products, so customers can browse and order accurately.

#### Acceptance Criteria

1. THE Platform SHALL support CRUD operations for:
   - Catalogs (e.g., "Lunch Menu", "Drinks", "Household Essentials")
   - Categories (e.g., "Burgers", "Pain Relief", "Fruits")
   - Products (name, description, images, price, availability, stock rules if enabled)
2. WHEN a product is out of stock or unavailable, THE Platform SHALL prevent ordering and show an availability message
3. WHEN a merchant updates catalogs/categories/products, THE Platform SHALL reflect changes on web/mobile within a defined refresh window (e.g., near real-time)
4. THE Platform SHALL support product modifiers/options where relevant (e.g., size, add-ons) per vertical configuration
5. THE Web_Backoffice SHALL provide an interface for merchants to manage catalogs, categories, and products with audit logging

### Requirement 3B: User Registration for Roles (Merchant, Courier, Client)

**User Story:** As a user, I want to register as a merchant, courier, or client, so I can use the platform according to my role.

#### Acceptance Criteria

1. THE Platform SHALL support registration flows for:
   - Merchant (company registration + verification fields)
   - Courier (identity + vehicle + availability + city)
   - Client (basic user account)
2. THE Platform SHALL enforce role-based access control (RBAC) so merchants cannot access courier tools, and vice versa
3. WHEN registration requires approval (merchant/courier), THE Platform SHALL support an approval workflow before enabling operations
4. THE Platform SHALL support login via OAuth2/OIDC for registered users
5. THE Platform SHALL allow guest users to browse and order without account creation (see Requirement 4A)

### Requirement 4: Order Management and Lifecycle

**User Story:** As a customer (registered or guest), I want to place orders and track progress from creation to delivery.

#### Acceptance Criteria

1. WHEN a customer places an order, THE Order_Management_System SHALL create an order with unique identification and initial status
2. THE Order_Management_System SHALL support orders from:
   - Registered clients
   - Guest checkout clients (see Requirement 4A)
3. WHEN order status changes occur, THE Order_Management_System SHALL update status and trigger appropriate notifications
4. WHEN orders are ready for dispatch, THE Order_Management_System SHALL integrate with the Dispatch_System for assignment
5. WHEN orders are completed, THE Order_Management_System SHALL finalize the transaction and update relevant systems, including delivery confirmation (see Requirement 7A)

### Requirement 4A: Guest Checkout (Order Without Login)

**User Story:** As a client visiting the website/app, I want to browse merchants, catalogs, and categories and place an order without logging in.

#### Acceptance Criteria

1. WHEN a guest user visits the platform, THE Platform SHALL allow browsing of:
   - Merchants (filtered by city/service area)
   - Catalogs and categories per merchant
   - Products and product details
2. WHEN a guest places an order, THE Platform SHALL require minimal checkout fields:
   - Delivery address (validated by Geospatial_Service)
   - Contact information (phone/email) for notifications and delivery coordination
3. WHEN a guest order is confirmed, THE Platform SHALL generate a Delivery Confirmation Code (DCC) and send it to the client via configured channels (SMS/push/email/WhatsApp)
4. THE Platform SHALL create a "guest identity" reference for order tracking (e.g., order link + verification token), without requiring full account creation
5. THE Platform SHALL support optional "convert guest to registered client" after ordering, without losing order history

### Requirement 5: Payment Processing and Multi-Currency

**User Story:** As a customer, I want to pay using local payment methods in my preferred currency, so that I can complete transactions conveniently and securely.

#### Acceptance Criteria

1. WHEN processing payments, THE Payment_Gateway SHALL support M-Pesa, Multibanco/MB Way, and international card payments
2. WHEN handling multi-currency transactions, THE Payment_Gateway SHALL support USD and MZN with real-time exchange rates
3. WHEN payment failures occur, THE Payment_Gateway SHALL provide clear error messages and retry mechanisms
4. WHEN refunds are requested, THE Payment_Gateway SHALL process refunds to the original payment method within business rules
5. WHEN storing payment data, THE Payment_Gateway SHALL encrypt sensitive information and comply with PCI DSS standards

### Requirement 6: Real-Time Tracking and Notifications

**User Story:** As a customer, I want to receive real-time updates about my order status and delivery progress, so that I can plan accordingly and stay informed.

#### Acceptance Criteria

1. WHEN order status changes, THE Notification_Service SHALL send real-time notifications to customers via push notifications and SMS
2. WHEN delivery is in progress, THE Tracking_Service SHALL provide real-time location updates with estimated arrival times
3. WHEN critical events occur, THE Notification_Service SHALL send immediate alerts to relevant stakeholders
4. WHEN customers request tracking information, THE Tracking_Service SHALL provide current status and location data
5. WHEN delivery is completed, THE Notification_Service SHALL send confirmation notifications with receipt information

### Requirement 7: Dispatch and Delivery Management

**User Story:** As a delivery coordinator, I want to efficiently assign and manage deliveries, so that orders are delivered promptly and cost-effectively.

#### Acceptance Criteria

1. WHEN orders are ready for delivery, THE Dispatch_System SHALL assign orders to available couriers based on location, capacity, and city
2. WHEN optimizing routes, THE Dispatch_System SHALL consider traffic, distance, and delivery windows
3. WHEN delivery issues arise, THE Dispatch_System SHALL provide escalation paths (reassign, cancel, partial delivery rules per vertical)
4. WHEN deliveries are completed, THE Dispatch_System SHALL update order status and collect delivery confirmation (see Requirement 7A)
5. THE courier mobile app SHALL support proof-of-delivery workflows

### Requirement 7A: Delivery Completion Using Confirmation Code (DCC)

**User Story:** As a client, I want a code to confirm delivery, so the courier can only complete delivery when I actually receive the package.

#### Acceptance Criteria

1. WHEN an order is confirmed, THE Platform SHALL generate a DCC (OTP/PIN) unique to the order
2. WHEN the courier attempts to complete delivery, THE courier app SHALL require entering the DCC
3. IF the entered DCC matches and is valid, THEN the system SHALL mark the order as Delivered and log the event in the Audit_System
4. IF the DCC is incorrect, THEN the system SHALL reject completion and enforce retry limits and lockout rules
5. THE Platform SHALL define DCC security rules:
   - Expiration policy (time-based and/or order-status based)
   - Max attempts per courier/order
   - Resend policy to client
   - Support/admin override process with audit logs

### Requirement 8: Analytics and Business Intelligence

**User Story:** As a business owner, I want to access comprehensive analytics about my operations, so that I can make data-driven decisions to improve my business.

#### Acceptance Criteria

1. WHEN generating reports, THE Analytics_Engine SHALL provide real-time dashboards with key performance indicators
2. WHEN analyzing trends, THE Analytics_Engine SHALL support historical data analysis with customizable time periods
3. WHEN comparing performance, THE Analytics_Engine SHALL provide benchmarking against industry standards and peer businesses
4. WHEN exporting data, THE Analytics_Engine SHALL support multiple formats (PDF, Excel, CSV) with scheduled delivery
5. WHEN accessing analytics, THE Analytics_Engine SHALL enforce tenant-specific data access and privacy controls

### Requirement 9: Compliance and Security

**User Story:** As a platform operator, I want to ensure full compliance with regulations and maintain high security standards, so that the platform operates legally and protects user data.

#### Acceptance Criteria

1. WHEN handling personal data, THE Compliance_Module SHALL enforce GDPR requirements including consent management and data portability
2. WHEN processing pharmacy orders, THE Compliance_Module SHALL validate prescriptions and enforce age verification requirements
3. WHEN storing sensitive data, THE Platform SHALL encrypt data at rest and in transit using industry-standard encryption
4. WHEN audit events occur, THE Audit_System SHALL log all critical operations with immutable audit trails
5. WHEN detecting suspicious activities, THE Platform SHALL implement fraud prevention measures and risk controls

### Requirement 10: Performance and Reliability

**User Story:** As a user, I want the platform to be fast and reliable, so that I can complete transactions efficiently without service interruptions.

#### Acceptance Criteria

1. THE Platform SHALL maintain 99.9% uptime with automated failover and recovery mechanisms
2. WHEN processing API requests, THE Platform SHALL respond within 300ms for 95% of core API calls
3. WHEN system load increases, THE Platform SHALL automatically scale resources to maintain performance levels
4. WHEN failures occur, THE Platform SHALL implement circuit breakers and graceful degradation
5. WHEN monitoring performance, THE Platform SHALL provide comprehensive observability with OpenTelemetry integration

### Requirement 11: Mobile Application Features

**User Story:** As a user (client/courier), I want to use intuitive mobile apps to browse, order, and manage deliveries, so that I can access services conveniently from my smartphone.

#### Acceptance Criteria

1. WHEN browsing products, THE Mobile_App SHALL provide intuitive search and filtering capabilities with vertical-specific categories
2. WHEN placing orders, THE Mobile_App SHALL support multi-store carts with clear pricing and delivery information
3. WHEN tracking orders, THE Mobile_App SHALL display real-time status updates with interactive maps
4. WHEN managing account, THE Mobile_App SHALL provide secure authentication with biometric support where available
5. WHEN offline, THE Mobile_App SHALL cache essential data and sync when connectivity is restored
6. Courier app MUST include "Complete delivery with DCC" flow
7. Client app/web MUST show "Your delivery confirmation code" and a secure way to retrieve/resend it

### Requirement 12: Web Backoffice Management

**User Story:** As a business administrator, I want to manage my business operations through a comprehensive web interface, so that I can efficiently oversee all aspects of my delivery service.

#### Acceptance Criteria

1. WHEN managing inventory, THE Web_Backoffice SHALL provide real-time inventory tracking with low-stock alerts
2. WHEN processing orders, THE Web_Backoffice SHALL display order queues with priority sorting and batch processing capabilities
3. WHEN analyzing performance, THE Web_Backoffice SHALL provide comprehensive dashboards with drill-down capabilities
4. WHEN managing staff, THE Web_Backoffice SHALL support role-based access control with audit logging
5. WHEN configuring settings, THE Web_Backoffice SHALL allow tenant-specific customization without technical expertise
6. Merchant backoffice MUST include catalog/category/product management (Requirement 3A)
7. Merchant backoffice MUST show order queue and status tracking for their store

### Requirement 13: Integration and API Management

**User Story:** As a system integrator, I want to integrate with external systems and services, so that the platform can connect with existing business tools and third-party services.

#### Acceptance Criteria

1. WHEN integrating with external systems, THE Platform SHALL provide RESTful APIs with comprehensive documentation
2. WHEN authenticating API access, THE Platform SHALL implement OAuth2/OIDC with proper scope management
3. WHEN processing API requests, THE Platform SHALL implement rate limiting and throttling to prevent abuse
4. WHEN API errors occur, THE Platform SHALL provide detailed error responses with troubleshooting guidance
5. WHEN versioning APIs, THE Platform SHALL maintain backward compatibility with clear deprecation policies
6. Public APIs for browsing merchants/catalogs/products and guest checkout
7. Authenticated APIs for merchant management and courier operations
8. Rate limiting particularly for guest checkout endpoints and DCC validation endpoints

### Requirement 14: Data Management and Backup

**User Story:** As a platform operator, I want to ensure data integrity and availability, so that business operations can continue without data loss.

#### Acceptance Criteria

1. WHEN storing data, THE Platform SHALL implement automated backup procedures with point-in-time recovery
2. WHEN data corruption is detected, THE Platform SHALL provide data validation and repair mechanisms
3. WHEN disaster recovery is needed, THE Platform SHALL restore operations within defined RTO and RPO targets
4. WHEN archiving data, THE Platform SHALL implement data retention policies with automated cleanup
5. WHEN migrating data, THE Platform SHALL ensure zero-downtime migrations with rollback capabilities