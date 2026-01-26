# Implementation Plan: Multi-Merchant Delivery Marketplace

## Overview

This implementation plan breaks down the multi-merchant delivery marketplace into discrete, manageable tasks that build incrementally toward a complete solution. The approach prioritizes MVP functionality with merchant onboarding, guest checkout, and secure delivery confirmation.

**Technology Stack**:
- **Backend**: Java 21 + Spring Boot + Spring Modulith + Hexagonal Architecture
- **Mobile Apps**: Flutter (Dart) for Android/iOS (Client & Courier apps)
- **Web Backoffice**: Angular 20 + TailwindCSS + NgRx (TypeScript) for Merchant management
- **Database**: PostgreSQL + PostGIS + Redis
- **Infrastructure**: Kafka, OAuth2/OIDC, OpenTelemetry

**MVP Priority**: Focus on merchant onboarding, catalog management, guest checkout, and secure delivery confirmation with DCC (Delivery Confirmation Codes).

**Current Status**: Backend core modules are implemented but need adaptation for multi-merchant architecture and guest checkout capabilities.

## Tasks

### Phase 1: Backend Architecture Adaptation

- [x] 1. Adapt Multi-Tenant to Multi-Merchant Architecture
  - [x] 1.1 Rename Tenant entities to Merchant entities
    - Update domain models, services, and repositories
    - Maintain backward compatibility during transition
    - Update database schema and migrations
    - _Requirements: 3, 3A, 3B_

  - [x] 1.2 Implement Merchant Registration and Onboarding
    - Create merchant registration workflow with approval process
    - Add business verification and document upload
    - Implement merchant status management (pending, approved, rejected, suspended)
    - _Requirements: 3, 3B_

  - [x] 1.3 Fix existing property tests for merchant isolation
    - Update TenantConfigurationIndependencePropertyTest to MerchantConfigurationIndependencePropertyTest
    - Fix failing assertions in merchant isolation tests
    - Ensure proper test data generation
    - _Requirements: 1.1, 1.3_

- [ ] 2. Catalog and Product Management Module
  - [x] 2.1 Implement Catalog domain model
    - Create Catalog aggregate with categories and products
    - Implement catalog lifecycle management
    - Add catalog visibility and status controls
    - _Requirements: 3A_

  - [x] 2.2 Implement Product Management
    - Create Product aggregate with modifiers and availability
    - Implement stock management and availability tracking
    - Add product image and media management
    - _Requirements: 3A_

  - [x] 2.3 Create Catalog Management APIs
    - Implement CRUD operations for catalogs, categories, products
    - Add merchant-specific access controls
    - Implement real-time catalog updates
    - _Requirements: 3A, 13_

  - [x] 2.4 Write property test for catalog data isolation
    - **Property: Catalog Data Isolation**
    - **Validates: Requirements 3A**

### Phase 2: Guest Checkout and Order Management

- [ ] 3. Guest Checkout Module
  - [x] 3.1 Implement Guest Order Creation
    - Create guest checkout flow without registration
    - Implement guest identity and tracking token generation
    - Add contact information validation and storage
    - _Requirements: 4A_

  - [x] 3.2 Implement Guest Order Tracking
    - Create secure order tracking with guest tokens
    - Implement order status updates for guest users
    - Add guest-to-registered user conversion
    - _Requirements: 4A_

  - [x] 3.3 Create Public Browsing APIs
    - Implement public merchant discovery endpoints
    - Create catalog and product browsing APIs
    - Add location-based merchant filtering
    - _Requirements: 4A, 13_

  - [ ]* 3.4 Write property test for guest checkout security
    - **Property: Guest Checkout Security**
    - **Validates: Requirements 4A**

- [ ] 4. Update Order Management for Multi-Merchant
  - [x] 4.1 Adapt Order entity for merchant-specific orders
    - Update Order aggregate to reference MerchantId instead of TenantId
    - Add support for guest orders with GuestInfo
    - Implement order routing to specific merchants
    - _Requirements: 4, 4A_

  - [x] 4.2 Implement Order State Machine
    - Create comprehensive order status transitions
    - Add merchant-specific order processing workflows
    - Implement order cancellation and refund logic
    - _Requirements: 4_

### Phase 3: Delivery Confirmation System

- [ ] 5. Delivery Confirmation Code (DCC) Module
  - [x] 5.1 Implement DCC Generation and Management
    - Create DeliveryConfirmationCode aggregate
    - Implement secure code generation algorithms with 4 digits
    - Add code expiration and retry limit management
    - _Requirements: 7A_

  - [x] 5.2 Implement DCC Validation and Security
    - Create code validation with attempt tracking
    - Implement lockout mechanisms for failed attempts
    - Add audit logging for all DCC operations
    - _Requirements: 7A, 9_

  - [x] 5.3 Create DCC APIs for Courier App
    - Implement delivery completion with DCC validation
    - Add code resend functionality
    - Create admin override capabilities with audit trails
    - _Requirements: 7A, 11_

  - [ ]* 5.4 Write property test for DCC security
    - **Property: DCC Security and Validation**
    - **Validates: Requirements 7A**

### Phase 4: Role-Based Access Control

- [ ] 6. User Management and RBAC
  - [x] 6.1 Implement Role-Based Authentication
    - Create user roles (Admin, Merchant, Courier, Client, Guest)
    - Implement OAuth2/OIDC with role-based claims
    - Add permission matrix enforcement
    - _Requirements: 3B, 13_

  - [x] 6.2 Implement Courier Registration and Management
    - Create courier onboarding workflow
    - Add vehicle and availability management
    - Implement courier approval process
    - _Requirements: 3B_

  - [x] 6.3 Update API Security
    - Implement role-based endpoint protection
    - Add rate limiting per role and endpoint
    - Create API key management for merchant integrations
    - _Requirements: 13_

### Phase 5: Mobile Applications

- [ ] 7. Flutter Client App
  - [x] 7.1 Set up Flutter project for Client app
    - Create Flutter project with BLoC architecture
    - Implement navigation and state management
    - Add authentication and guest mode support
    - _Requirements: 11_

  - [x] 7.2 Implement Public Browsing Features
    - Create merchant discovery and browsing screens
    - Implement catalog and product browsing
    - Add search and filtering capabilities
    - _Requirements: 4A, 11_

  - [ ] 7.3 Implement Guest Checkout Flow
    - Create guest order placement screens
    - Implement delivery address and contact forms
    - Add order confirmation and DCC display
    - _Requirements: 4A, 11_

  - [ ] 7.4 Implement Order Tracking
    - Create order tracking screens for guests and registered users
    - Add real-time status updates
    - Implement DCC management (view, resend)
    - _Requirements: 4A, 11_

- [ ] 8. Flutter Courier App
  - [ ] 8.1 Set up Flutter project for Courier app
    - Create separate Flutter project for courier operations
    - Implement courier authentication and profile management
    - Add location services and real-time tracking
    - _Requirements: 11_

  - [ ] 8.2 Implement Delivery Management
    - Create delivery assignment and acceptance screens
    - Implement route optimization and navigation
    - Add delivery status update capabilities
    - _Requirements: 7, 11_

  - [ ] 8.3 Implement DCC Delivery Completion
    - Create delivery completion screen with DCC input
    - Implement code validation and error handling
    - Add proof of delivery capture (photo, signature)
    - _Requirements: 7A, 11_

### Phase 6: Merchant Web Backoffice

- [ ] 9. Angular Merchant Backoffice
  - [ ] 9.1 Set up Angular 20 project
    - Create Angular project with proper architecture
    - Set up TailwindCSS and component library
    - Configure NgRx for state management
    - Implement merchant authentication
    - _Requirements: 12_

  - [ ] 9.2 Implement Merchant Dashboard
    - Create merchant overview dashboard
    - Add order queue and status management
    - Implement real-time notifications
    - _Requirements: 12_

  - [ ] 9.3 Implement Catalog Management Interface
    - Create catalog, category, and product management screens
    - Add image upload and media management
    - Implement bulk operations and import/export
    - _Requirements: 3A, 12_

  - [ ] 9.4 Implement Order Management Interface
    - Create order list and detail views
    - Add order status management and processing
    - Implement merchant-specific analytics
    - _Requirements: 12_

### Phase 7: Integration and Testing

- [ ] 10. End-to-End Integration
  - [ ] 10.1 Implement Guest Checkout E2E Flow
    - Test complete guest checkout workflow
    - Validate DCC generation and delivery
    - Test order tracking and status updates
    - _Requirements: 4A, 7A_

  - [ ] 10.2 Implement Delivery Completion E2E Flow
    - Test courier delivery completion with DCC
    - Validate security measures and retry limits
    - Test admin override and audit logging
    - _Requirements: 7A_

  - [ ] 10.3 Multi-Merchant Integration Testing
    - Test merchant isolation and data security
    - Validate role-based access controls
    - Test concurrent operations across merchants
    - _Requirements: 1.1, 3, 13_

- [ ] 11. Security and Performance Testing
  - [ ] 11.1 Security Testing
    - Test guest checkout security measures
    - Validate DCC security and fraud prevention
    - Test role-based access control enforcement
    - _Requirements: 7A, 9, 13_

  - [ ] 11.2 Performance and Load Testing
    - Test system performance under load
    - Validate database performance with merchant isolation
    - Test API rate limiting and throttling
    - _Requirements: 10, 13_

### Phase 8: Deployment and Monitoring

- [ ] 12. Production Deployment
  - [ ] 12.1 Containerization and Infrastructure
    - Create Docker containers for all services
    - Set up Kubernetes deployment configurations
    - Implement database migration strategies
    - _Requirements: All_

  - [ ] 12.2 Monitoring and Observability
    - Set up comprehensive logging and monitoring
    - Implement DCC audit trail monitoring
    - Add merchant onboarding workflow tracking
    - _Requirements: 9, 10_

## Notes

- Tasks marked with `*` are optional property-based tests that can be skipped for faster MVP delivery
- Each task references specific requirements for traceability
- The implementation follows hexagonal architecture principles with proper separation of concerns
- Multi-merchant isolation is enforced at all levels (database, application, API)
- Guest checkout security is prioritized with comprehensive audit logging
- DCC (Delivery Confirmation Code) system ensures secure delivery completion
- Role-based access control ensures proper separation of merchant, courier, and client operations

- [ ] 11. Flutter Mobile Application Foundation
  - [ ] 11.1 Set up Flutter project structure
    - Create Flutter project with proper architecture (BLoC pattern)
    - Set up dependency injection and state management
    - Configure navigation and routing
    - Add authentication and secure storage
    - _Requirements: 11.4_

  - [ ] 11.2 Implement core mobile services
    - Create API client with authentication
    - Implement location services and permissions
    - Add push notification handling
    - Set up offline data caching and synchronization
    - _Requirements: 11.5_

  - [ ]* 11.3 Write property test for offline data synchronization
    - **Property 36: Offline Data Synchronization**
    - **Validates: Requirements 11.5**

- [ ] 12. Mobile App Core Features
  - [ ] 12.1 Implement product browsing and search
    - Create product catalog screens
    - Implement search with vertical-specific filters
    - Add location-based business discovery
    - _Requirements: 11.1, 2.3_

  - [ ]* 12.2 Write property test for mobile search and filtering
    - **Property 33: Mobile Search and Filtering**
    - **Validates: Requirements 11.1**

  - [ ] 12.3 Implement shopping cart and checkout
    - Create multi-store cart functionality
    - Implement order creation and payment integration
    - Add delivery address selection and validation
    - _Requirements: 11.2_

  - [ ]* 12.4 Write property test for multi-store cart calculation
    - **Property 34: Multi-Store Cart Calculation**
    - **Validates: Requirements 11.2**

  - [ ] 12.5 Implement order tracking and status
    - Create real-time order tracking screens
    - Add interactive maps for delivery tracking
    - Implement push notification handling for status updates
    - _Requirements: 11.3_

- [ ] 13. Angular Web Backoffice Foundation
  - [ ] 13.1 Set up Angular 20 project with proper architecture
    - Create Angular project with strict separation of concerns
    - Set up TailwindCSS and component library
    - Configure NgRx for state management
    - Implement authentication and route guards
    - _Requirements: 12.4_

  - [ ] 13.2 Create shared components and services
    - Build reusable UI components (separate .ts, .html, .css files)
    - Implement API services with proper error handling
    - Create data models and interfaces
    - Set up HTTP interceptors for authentication and tenant context
    - _Requirements: 12.4_

- [ ] 14. Web Backoffice Core Features
  - [ ] 14.1 Implement order management interface
    - Create order list and detail components
    - Implement order status management
    - Add batch processing capabilities
    - _Requirements: 12.2_

  - [ ]* 14.2 Write property test for order queue management
    - **Property 38: Order Queue Management**
    - **Validates: Requirements 12.2**

  - [ ] 14.3 Implement inventory management
    - Create inventory tracking components
    - Add low-stock alert system
    - Implement product catalog management
    - _Requirements: 12.1_

  - [ ]* 14.4 Write property test for inventory alert generation
    - **Property 37: Inventory Alert Generation**
    - **Validates: Requirements 12.1**

  - [ ] 14.5 Implement analytics dashboard
    - Create real-time analytics components
    - Add customizable reporting features
    - Implement data export functionality
    - _Requirements: 8.1, 8.4_

- [ ] 15. Integration and System Testing
  - [ ] 15.1 Implement end-to-end order workflow
    - Connect mobile app order creation to backend processing
    - Integrate payment processing with order fulfillment
    - Connect dispatch system with delivery tracking
    - _Requirements: 4.4, 4.5_

  - [ ]* 15.2 Write property test for order lifecycle integration
    - **Property 12: Order Lifecycle Integration**
    - **Validates: Requirements 4.4, 4.5**

  - [ ] 15.3 Implement multi-tenant integration testing
    - Test tenant isolation across all components
    - Validate tenant-specific configurations
    - Test cross-tenant security boundaries
    - _Requirements: 1.1, 1.2, 1.3_

  - [ ]* 15.4 Write comprehensive tenant isolation integration tests
    - Test data isolation across all modules
    - Validate configuration independence
    - Test security boundary enforcement

- [ ] 16. Performance and Observability
  - [ ] 16.1 Implement monitoring and observability
    - Set up OpenTelemetry tracing and metrics
    - Add application performance monitoring
    - Implement health checks and readiness probes
    - _Requirements: 10.5_

  - [ ]* 16.2 Write property test for observability data generation
    - **Property 32: Observability Data Generation**
    - **Validates: Requirements 10.5**

  - [ ] 16.3 Implement circuit breakers and resilience patterns
    - Add circuit breakers for external service calls
    - Implement retry policies and timeouts
    - Add graceful degradation for service failures
    - _Requirements: 10.4_

  - [ ]* 16.4 Write property test for circuit breaker activation
    - **Property 31: Circuit Breaker Activation**
    - **Validates: Requirements 10.4**

- [ ] 17. Final Integration and Deployment Preparation
  - [ ] 17.1 Complete system integration
    - Wire all components together
    - Implement final API integrations
    - Add comprehensive error handling
    - _Requirements: All integrated requirements_

  - [ ] 17.2 Prepare deployment configuration
    - Create Docker containers for all services
    - Set up database migration scripts
    - Configure environment-specific settings
    - Add deployment health checks

- [ ] 18. Final Checkpoint - Complete System
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional property-based tests that can be skipped for faster MVP delivery
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation and provide opportunities for user feedback
- Property tests validate universal correctness properties using jqwik framework
- Unit tests validate specific examples and edge cases
- The implementation follows hexagonal architecture principles with proper separation of concerns
- Multi-tenant isolation is enforced at all levels (database, application, API)
- The MVP focuses on restaurants and grocery verticals with single-city support
- Full multi-city and multi-vertical support can be added in subsequent iterations
- **Backend Status**: Core modules are implemented but property tests need fixes before proceeding to frontend development

- [ ] 12. Flutter Mobile Application Foundation
  - [ ] 12.1 Set up Flutter project structure
    - Create Flutter project with proper architecture (BLoC pattern)
    - Set up dependency injection and state management
    - Configure navigation and routing
    - Add authentication and secure storage
    - _Requirements: 11.4_

  - [ ] 12.2 Implement core mobile services
    - Create API client with authentication
    - Implement location services and permissions
    - Add push notification handling
    - Set up offline data caching and synchronization
    - _Requirements: 11.5_

  - [ ]* 12.3 Write property test for offline data synchronization
    - **Property 36: Offline Data Synchronization**
    - **Validates: Requirements 11.5**

- [ ] 13. Mobile App Core Features
  - [ ] 13.1 Implement product browsing and search
    - Create product catalog screens
    - Implement search with vertical-specific filters
    - Add location-based business discovery
    - _Requirements: 11.1, 2.3_

  - [ ]* 13.2 Write property test for mobile search and filtering
    - **Property 33: Mobile Search and Filtering**
    - **Validates: Requirements 11.1**

  - [ ] 13.3 Implement shopping cart and checkout
    - Create multi-store cart functionality
    - Implement order creation and payment integration
    - Add delivery address selection and validation
    - _Requirements: 11.2_

  - [ ]* 13.4 Write property test for multi-store cart calculation
    - **Property 34: Multi-Store Cart Calculation**
    - **Validates: Requirements 11.2**

  - [ ] 13.5 Implement order tracking and status
    - Create real-time order tracking screens
    - Add interactive maps for delivery tracking
    - Implement push notification handling for status updates
    - _Requirements: 11.3_

- [ ] 14. Angular Web Backoffice Foundation
  - [ ] 14.1 Set up Angular 20 project with proper architecture
    - Create Angular project with strict separation of concerns
    - Set up TailwindCSS and component library
    - Configure NgRx for state management
    - Implement authentication and route guards
    - _Requirements: 12.4_

  - [ ] 14.2 Create shared components and services
    - Build reusable UI components (separate .ts, .html, .css files)
    - Implement API services with proper error handling
    - Create data models and interfaces
    - Set up HTTP interceptors for authentication and tenant context
    - _Requirements: 12.4_

- [ ] 15. Web Backoffice Core Features
  - [ ] 15.1 Implement order management interface
    - Create order list and detail components
    - Implement order status management
    - Add batch processing capabilities
    - _Requirements: 12.2_

  - [ ]* 15.2 Write property test for order queue management
    - **Property 38: Order Queue Management**
    - **Validates: Requirements 12.2**

  - [ ] 15.3 Implement inventory management
    - Create inventory tracking components
    - Add low-stock alert system
    - Implement product catalog management
    - _Requirements: 12.1_

  - [ ]* 15.4 Write property test for inventory alert generation
    - **Property 37: Inventory Alert Generation**
    - **Validates: Requirements 12.1**

  - [ ] 15.5 Implement analytics dashboard
    - Create real-time analytics components
    - Add customizable reporting features
    - Implement data export functionality
    - _Requirements: 8.1, 8.4_

- [ ] 16. Integration and System Testing
  - [ ] 16.1 Implement end-to-end order workflow
    - Connect mobile app order creation to backend processing
    - Integrate payment processing with order fulfillment
    - Connect dispatch system with delivery tracking
    - _Requirements: 4.4, 4.5_

  - [ ]* 16.2 Write property test for order lifecycle integration
    - **Property 12: Order Lifecycle Integration**
    - **Validates: Requirements 4.4, 4.5**

  - [ ] 16.3 Implement multi-tenant integration testing
    - Test tenant isolation across all components
    - Validate tenant-specific configurations
    - Test cross-tenant security boundaries
    - _Requirements: 1.1, 1.2, 1.3_

  - [ ]* 16.4 Write comprehensive tenant isolation integration tests
    - Test data isolation across all modules
    - Validate configuration independence
    - Test security boundary enforcement

- [ ] 17. Performance and Observability
  - [ ] 17.1 Implement monitoring and observability
    - Set up OpenTelemetry tracing and metrics
    - Add application performance monitoring
    - Implement health checks and readiness probes
    - _Requirements: 10.5_

  - [ ]* 17.2 Write property test for observability data generation
    - **Property 32: Observability Data Generation**
    - **Validates: Requirements 10.5**

  - [ ] 17.3 Implement circuit breakers and resilience patterns
    - Add circuit breakers for external service calls
    - Implement retry policies and timeouts
    - Add graceful degradation for service failures
    - _Requirements: 10.4_

  - [ ]* 17.4 Write property test for circuit breaker activation
    - **Property 31: Circuit Breaker Activation**
    - **Validates: Requirements 10.4**

- [ ] 18. Final Integration and Deployment Preparation
  - [ ] 18.1 Complete system integration
    - Wire all components together
    - Implement final API integrations
    - Add comprehensive error handling
    - _Requirements: All integrated requirements_

  - [ ] 18.2 Prepare deployment configuration
    - Create Docker containers for all services
    - Set up database migration scripts
    - Configure environment-specific settings
    - Add deployment health checks

- [ ] 19. Final Checkpoint - Complete System
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional property-based tests that can be skipped for faster MVP delivery
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation and provide opportunities for user feedback
- Property tests validate universal correctness properties using jqwik framework
- Unit tests validate specific examples and edge cases
- The implementation follows hexagonal architecture principles with proper separation of concerns
- Multi-tenant isolation is enforced at all levels (database, application, API)
- The MVP focuses on restaurants and grocery verticals with single-city support
- Full multi-city and multi-vertical support can be added in subsequent iterations