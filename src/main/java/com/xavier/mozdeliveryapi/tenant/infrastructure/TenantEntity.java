package com.xavier.mozdeliveryapi.tenant.infrastructure;

import com.xavier.mozdeliveryapi.tenant.domain.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for Tenant aggregate.
 */
@Entity
@Table(name = "tenants")
public class TenantEntity {
    
    @Id
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Vertical vertical;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantStatus status;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> configuration;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "compliance_settings", columnDefinition = "jsonb")
    private Map<String, Object> complianceSettings;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Default constructor for JPA
    protected TenantEntity() {}
    
    // Constructor for creating from domain object
    public TenantEntity(Tenant tenant) {
        this.id = tenant.getTenantId().value();
        this.name = tenant.getName();
        this.vertical = tenant.getVertical();
        this.status = tenant.getStatus();
        this.configuration = mapConfigurationToJson(tenant.getConfiguration());
        this.complianceSettings = mapComplianceToJson(tenant.getComplianceSettings());
        this.createdAt = tenant.getCreatedAt();
        this.updatedAt = tenant.getUpdatedAt();
    }
    
    /**
     * Convert to domain object.
     */
    public Tenant toDomain() {
        TenantConfiguration config = mapJsonToConfiguration(configuration);
        ComplianceSettings compliance = mapJsonToCompliance(complianceSettings);
        
        return new Tenant(
            TenantId.of(id),
            name,
            vertical,
            status,
            config,
            compliance,
            createdAt,
            updatedAt
        );
    }
    
    /**
     * Update from domain object.
     */
    public void updateFrom(Tenant tenant) {
        this.name = tenant.getName();
        this.vertical = tenant.getVertical();
        this.status = tenant.getStatus();
        this.configuration = mapConfigurationToJson(tenant.getConfiguration());
        this.complianceSettings = mapComplianceToJson(tenant.getComplianceSettings());
        this.updatedAt = tenant.getUpdatedAt();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> mapConfigurationToJson(TenantConfiguration config) {
        return Map.of(
            "deliveryFee", config.deliveryFee().toString(),
            "minimumOrderAmount", config.minimumOrderAmount().toString(),
            "maxDeliveryTime", config.maxDeliveryTime().toString(),
            "acceptsCashPayments", config.acceptsCashPayments(),
            "acceptsCardPayments", config.acceptsCardPayments(),
            "acceptsMobilePayments", config.acceptsMobilePayments(),
            "customSettings", config.customSettings()
        );
    }
    
    @SuppressWarnings("unchecked")
    private TenantConfiguration mapJsonToConfiguration(Map<String, Object> json) {
        if (json == null) {
            return TenantConfiguration.defaultFor(vertical);
        }
        
        return new TenantConfiguration(
            new BigDecimal(json.get("deliveryFee").toString()),
            new BigDecimal(json.get("minimumOrderAmount").toString()),
            Duration.parse(json.get("maxDeliveryTime").toString()),
            (Boolean) json.get("acceptsCashPayments"),
            (Boolean) json.get("acceptsCardPayments"),
            (Boolean) json.get("acceptsMobilePayments"),
            (Map<String, Object>) json.getOrDefault("customSettings", Map.of())
        );
    }
    
    private Map<String, Object> mapComplianceToJson(ComplianceSettings compliance) {
        return Map.of(
            "gdprEnabled", compliance.gdprEnabled(),
            "auditLoggingEnabled", compliance.auditLoggingEnabled(),
            "dataEncryptionRequired", compliance.dataEncryptionRequired(),
            "verticalSpecificSettings", compliance.verticalSpecificSettings()
        );
    }
    
    @SuppressWarnings("unchecked")
    private ComplianceSettings mapJsonToCompliance(Map<String, Object> json) {
        if (json == null) {
            return ComplianceSettings.defaultFor(vertical);
        }
        
        return new ComplianceSettings(
            (Boolean) json.get("gdprEnabled"),
            (Boolean) json.get("auditLoggingEnabled"),
            (Boolean) json.get("dataEncryptionRequired"),
            (Map<String, Object>) json.getOrDefault("verticalSpecificSettings", Map.of())
        );
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public Vertical getVertical() { return vertical; }
    public TenantStatus getStatus() { return status; }
    public Map<String, Object> getConfiguration() { return configuration; }
    public Map<String, Object> getComplianceSettings() { return complianceSettings; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}