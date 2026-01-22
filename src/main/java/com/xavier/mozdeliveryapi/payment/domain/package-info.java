/**
 * Payment domain layer containing business logic, entities, value objects, and domain services.
 * 
 * This package implements the core payment processing functionality including:
 * - Payment aggregate and lifecycle management
 * - Multi-currency support with exchange rates
 * - Payment gateway abstractions for M-Pesa, Multibanco, and card payments
 * - Refund processing capabilities
 * - Domain events for payment state changes
 * 
 * The domain layer is independent of infrastructure concerns and focuses on
 * business rules and invariants related to payment processing.
 */
package com.xavier.mozdeliveryapi.payment.domain;