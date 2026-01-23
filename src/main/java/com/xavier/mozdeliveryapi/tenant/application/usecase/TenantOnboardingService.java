package com.xavier.mozdeliveryapi.tenant.application.usecase;

import com.xavier.mozdeliveryapi.shared.application.usecase.port.DomainEventPublisher;
import com.xavier.mozdeliveryapi.shared.application.usecase.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xavier.mozdeliveryapi.tenant.application.dto.TenantOnboardingRequest;
import com.xavier.mozdeliveryapi.tenant.application.dto.TenantOnboardingResponse;
import com.xavier.mozdeliveryapi.tenant.domain.entity.Tenant;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.ComplianceSettings;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantConfiguration;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantId;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.TenantStatus;
import com.xavier.mozdeliveryapi.tenant.domain.valueobject.Vertical;
import com.xavier.mozdeliveryapi.tenant.application.usecase.port.TenantRepository;

/**
 * Application service for tenant onboarding workflow.
 */
@Service
public class TenantOnboardingService implements UseCase<TenantOnboardingRequest, TenantOnboardingResponse> {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantOnboardingService.class);
    
    private final TenantRepository tenantRepository;
    private final DomainEventPublisher eventPublisher;
    
    public TenantOnboardingService(TenantRepository tenantRepository, 
                                   DomainEventPublisher eventPublisher) {
        this.tenantRepository = tenantRepository;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Execute the use case - onboard a new tenant to the platform.
     */
    @Override
    public TenantOnboardingResponse execute(TenantOnboardingRequest request) {
        return onboardTenant(request);
    }
    
    /**
     * Onboard a new tenant to the platform.
     */
    @Transactional
    public TenantOnboardingResponse onboardTenant(TenantOnboardingRequest request) {
        logger.info("Starting tenant onboarding for: {}", request.tenantName());
        
        try {
            // Check if tenant name already exists
            if (tenantRepository.existsByName(request.tenantName())) {
                logger.warn("Tenant onboarding failed: name already exists - {}", request.tenantName());
                return TenantOnboardingResponse.failure("Tenant name already exists");
            }
            
            // Create new tenant with vertical-specific configuration
            TenantId tenantId = TenantId.generate();
            Tenant tenant = new Tenant(tenantId, request.tenantName(), request.vertical());
            
            // Apply vertical-specific business rules
            applyVerticalSpecificRules(tenant, request);
            
            // Save tenant
            Tenant savedTenant = tenantRepository.save(tenant);
            
            // Publish domain events
            publishDomainEvents(savedTenant);
            
            logger.info("Tenant onboarding completed successfully for: {} with ID: {}", 
                       request.tenantName(), tenantId);
            
            return TenantOnboardingResponse.success(
                savedTenant.getTenantId(),
                savedTenant.getName(),
                savedTenant.getVertical(),
                savedTenant.getCreatedAt()
            );
            
        } catch (Exception e) {
            logger.error("Tenant onboarding failed for: {}", request.tenantName(), e);
            return TenantOnboardingResponse.failure("Onboarding failed: " + e.getMessage());
        }
    }
    
    /**
     * Apply vertical-specific business rules during onboarding.
     */
    private void applyVerticalSpecificRules(Tenant tenant, TenantOnboardingRequest request) {
        Vertical vertical = request.vertical();
        
        // Apply vertical-specific configuration adjustments
        switch (vertical) {
            case PHARMACY -> {
                // Pharmacy-specific rules
                logger.info("Applying pharmacy-specific onboarding rules for tenant: {}", 
                           request.tenantName());
                
                // Ensure compliance settings are properly configured
                ComplianceSettings pharmacyCompliance = ComplianceSettings.defaultFor(Vertical.PHARMACY);
                tenant.updateComplianceSettings(pharmacyCompliance);
                
                // Pharmacy tenants cannot accept cash payments by default
                TenantConfiguration config = tenant.getConfiguration();
                TenantConfiguration pharmacyConfig = new TenantConfiguration(
                    config.deliveryFee(),
                    config.minimumOrderAmount(),
                    config.maxDeliveryTime(),
                    false, // No cash payments
                    config.acceptsCardPayments(),
                    config.acceptsMobilePayments(),
                    config.customSettings()
                );
                tenant.updateConfiguration(pharmacyConfig);
            }
            
            case BEVERAGES -> {
                // Beverages-specific rules (age verification required)
                logger.info("Applying beverages-specific onboarding rules for tenant: {}", 
                           request.tenantName());
                
                ComplianceSettings beverageCompliance = ComplianceSettings.defaultFor(Vertical.BEVERAGES);
                tenant.updateComplianceSettings(beverageCompliance);
            }
            
            case RESTAURANT -> {
                // Restaurant-specific rules
                logger.info("Applying restaurant-specific onboarding rules for tenant: {}", 
                           request.tenantName());
                
                // Restaurants typically have shorter delivery times
                TenantConfiguration config = tenant.getConfiguration();
                TenantConfiguration restaurantConfig = new TenantConfiguration(
                    config.deliveryFee(),
                    config.minimumOrderAmount(),
                    java.time.Duration.ofMinutes(30), // Faster delivery for restaurants
                    config.acceptsCashPayments(),
                    config.acceptsCardPayments(),
                    config.acceptsMobilePayments(),
                    java.util.Map.of("preparationTime", java.time.Duration.ofMinutes(15))
                );
                tenant.updateConfiguration(restaurantConfig);
            }
            
            default -> {
                // Default configuration is already applied
                logger.info("Applying default onboarding rules for tenant: {} with vertical: {}", 
                           request.tenantName(), vertical);
            }
        }
    }
    
    /**
     * Publish domain events from the tenant aggregate.
     */
    private void publishDomainEvents(Tenant tenant) {
        tenant.getDomainEvents().forEach(event -> {
            logger.debug("Publishing domain event: {}", event.getClass().getSimpleName());
            eventPublisher.publish(event);
        });
        tenant.clearDomainEvents();
    }
    
    /**
     * Get tenant onboarding statistics.
     */
    public TenantOnboardingStats getOnboardingStats() {
        long totalTenants = tenantRepository.countByStatus(TenantStatus.ACTIVE) +
                           tenantRepository.countByStatus(TenantStatus.INACTIVE) +
                           tenantRepository.countByStatus(TenantStatus.SUSPENDED);
        
        long activeTenants = tenantRepository.countByStatus(TenantStatus.ACTIVE);
        
        return new TenantOnboardingStats(
            totalTenants,
            activeTenants,
            tenantRepository.countByVertical(Vertical.RESTAURANT),
            tenantRepository.countByVertical(Vertical.GROCERY),
            tenantRepository.countByVertical(Vertical.PHARMACY),
            tenantRepository.countByVertical(Vertical.CONVENIENCE),
            tenantRepository.countByVertical(Vertical.ELECTRONICS),
            tenantRepository.countByVertical(Vertical.FLORIST),
            tenantRepository.countByVertical(Vertical.BEVERAGES),
            tenantRepository.countByVertical(Vertical.FUEL_STATION)
        );
    }
}
