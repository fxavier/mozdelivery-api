package com.xavier.mozdeliveryapi.merchant.infra.persistence;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.xavier.mozdeliveryapi.merchant.domain.entity.Merchant;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.ApprovalStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.BusinessDetails;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.ComplianceSettings;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantConfiguration;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantId;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.MerchantStatus;
import com.xavier.mozdeliveryapi.merchant.domain.valueobject.Vertical;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity for Merchant aggregate.
 */
@Entity
@Table(name = "merchants")
public class MerchantEntity {
    
    @Id
    private UUID id;
    
    @Column(name = "business_name", nullable = false)
    private String businessName;
    
    @Column(name = "display_name", nullable = false)
    private String displayName;
    
    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;
    
    @Column(name = "tax_id")
    private String taxId;
    
    @Column(name = "contact_email", nullable = false)
    private String contactEmail;
    
    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;
    
    @Column(name = "business_address", nullable = false)
    private String businessAddress;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String country;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Vertical vertical;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantStatus status;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> configuration;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "compliance_settings", columnDefinition = "jsonb")
    private Map<String, Object> complianceSettings;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "approval_status", columnDefinition = "jsonb")
    private Map<String, Object> approvalStatus;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Default constructor for JPA
    protected MerchantEntity() {}
    
    // Constructor for creating from domain object
    public MerchantEntity(Merchant merchant) {
        this.id = merchant.getMerchantId().value();
        this.businessName = merchant.getBusinessDetails().businessName();
        this.displayName = merchant.getBusinessDetails().displayName();
        this.businessRegistrationNumber = merchant.getBusinessDetails().businessRegistrationNumber();
        this.taxId = merchant.getBusinessDetails().taxId();
        this.contactEmail = merchant.getBusinessDetails().contactEmail();
        this.contactPhone = merchant.getBusinessDetails().contactPhone();
        this.businessAddress = merchant.getBusinessDetails().businessAddress();
        this.city = merchant.getBusinessDetails().city();
        this.country = merchant.getBusinessDetails().country();
        this.vertical = merchant.getVertical();
        this.status = merchant.getStatus();
        this.configuration = mapConfigurationToJson(merchant.getConfiguration());
        this.complianceSettings = mapComplianceToJson(merchant.getComplianceSettings());
        this.approvalStatus = mapApprovalStatusToJson(merchant.getApprovalStatus());
        this.createdAt = merchant.getCreatedAt();
        this.updatedAt = merchant.getUpdatedAt();
    }
    
    /**
     * Convert to domain object.
     */
    public Merchant toDomain() {
        BusinessDetails businessDetails = new BusinessDetails(
            businessName, displayName, businessRegistrationNumber, taxId,
            contactEmail, contactPhone, businessAddress, city, country
        );
        
        MerchantConfiguration config = mapJsonToConfiguration(configuration);
        ComplianceSettings compliance = mapJsonToCompliance(complianceSettings);
        ApprovalStatus approval = mapJsonToApprovalStatus(approvalStatus);
        
        return new Merchant(
            MerchantId.of(id),
            businessDetails,
            vertical,
            status,
            config,
            compliance,
            approval,
            createdAt,
            updatedAt
        );
    }
    
    /**
     * Update from domain object.
     */
    public void updateFrom(Merchant merchant) {
        this.businessName = merchant.getBusinessDetails().businessName();
        this.displayName = merchant.getBusinessDetails().displayName();
        this.businessRegistrationNumber = merchant.getBusinessDetails().businessRegistrationNumber();
        this.taxId = merchant.getBusinessDetails().taxId();
        this.contactEmail = merchant.getBusinessDetails().contactEmail();
        this.contactPhone = merchant.getBusinessDetails().contactPhone();
        this.businessAddress = merchant.getBusinessDetails().businessAddress();
        this.city = merchant.getBusinessDetails().city();
        this.country = merchant.getBusinessDetails().country();
        this.vertical = merchant.getVertical();
        this.status = merchant.getStatus();
        this.configuration = mapConfigurationToJson(merchant.getConfiguration());
        this.complianceSettings = mapComplianceToJson(merchant.getComplianceSettings());
        this.approvalStatus = mapApprovalStatusToJson(merchant.getApprovalStatus());
        this.updatedAt = merchant.getUpdatedAt();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> mapConfigurationToJson(MerchantConfiguration config) {
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
    private MerchantConfiguration mapJsonToConfiguration(Map<String, Object> json) {
        if (json == null) {
            return MerchantConfiguration.defaultFor(vertical);
        }
        
        return new MerchantConfiguration(
            new java.math.BigDecimal(json.get("deliveryFee").toString()),
            new java.math.BigDecimal(json.get("minimumOrderAmount").toString()),
            java.time.Duration.parse(json.get("maxDeliveryTime").toString()),
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
    
    private Map<String, Object> mapApprovalStatusToJson(ApprovalStatus approval) {
        return Map.of(
            "status", approval.status().name(),
            "reason", approval.reason() != null ? approval.reason() : "",
            "approvedBy", approval.approvedBy() != null ? approval.approvedBy() : "",
            "approvedAt", approval.approvedAt() != null ? approval.approvedAt().toString() : "",
            "rejectionReason", approval.rejectionReason() != null ? approval.rejectionReason() : ""
        );
    }
    
    private ApprovalStatus mapJsonToApprovalStatus(Map<String, Object> json) {
        if (json == null) {
            return ApprovalStatus.pending();
        }
        
        MerchantStatus status = MerchantStatus.valueOf(json.get("status").toString());
        String reason = json.get("reason").toString();
        String approvedBy = json.get("approvedBy").toString();
        String approvedAtStr = json.get("approvedAt").toString();
        String rejectionReason = json.get("rejectionReason").toString();
        
        Instant approvedAt = approvedAtStr.isEmpty() ? null : Instant.parse(approvedAtStr);
        
        return new ApprovalStatus(
            status,
            reason.isEmpty() ? null : reason,
            approvedBy.isEmpty() ? null : approvedBy,
            approvedAt,
            rejectionReason.isEmpty() ? null : rejectionReason
        );
    }
    
    // Getters
    public UUID getId() { return id; }
    public String getBusinessName() { return businessName; }
    public String getDisplayName() { return displayName; }
    public String getBusinessRegistrationNumber() { return businessRegistrationNumber; }
    public String getTaxId() { return taxId; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public String getBusinessAddress() { return businessAddress; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public Vertical getVertical() { return vertical; }
    public MerchantStatus getStatus() { return status; }
    public Map<String, Object> getConfiguration() { return configuration; }
    public Map<String, Object> getComplianceSettings() { return complianceSettings; }
    public Map<String, Object> getApprovalStatus() { return approvalStatus; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}