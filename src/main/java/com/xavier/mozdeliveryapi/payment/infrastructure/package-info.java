/**
 * Payment infrastructure layer containing concrete implementations of payment gateways,
 * encryption services, and external service integrations.
 * 
 * This package implements:
 * - M-Pesa payment gateway integration
 * - Multibanco/MB Way payment gateway integration  
 * - Card payment gateway integration with 3D Secure support
 * - PCI DSS compliant encryption services
 * - Payment gateway factory for method selection
 * - Configuration for payment service providers
 * 
 * All implementations follow the hexagonal architecture pattern by implementing
 * domain interfaces and handling external service communication.
 */
package com.xavier.mozdeliveryapi.payment.infrastructure;