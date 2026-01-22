/**
 * Order Management Module
 * 
 * This module handles the complete order lifecycle from creation to completion,
 * including order validation, status management, and business rule enforcement.
 * 
 * Key components:
 * - Order aggregate: Core order entity with lifecycle management
 * - Order repository: Data access with tenant isolation
 * - Order service: Business logic and validation
 * - Order events: Domain events for order state changes
 */
package com.xavier.mozdeliveryapi.order;