package com.xavier.mozdeliveryapi.compliance.infra.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xavier.mozdeliveryapi.compliance.application.usecase.ComplianceApplicationService;
import com.xavier.mozdeliveryapi.compliance.application.dto.ConsentResponse;
import com.xavier.mozdeliveryapi.compliance.application.dto.DataDeletionRequestRequest;
import com.xavier.mozdeliveryapi.compliance.application.dto.DataPortabilityRequestRequest;
import com.xavier.mozdeliveryapi.compliance.application.dto.GiveConsentRequest;
import com.xavier.mozdeliveryapi.shared.application.usecase.TenantContext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.xavier.mozdeliveryapi.compliance.domain.entity.Consent;

/**
 * REST controller for compliance operations.
 */
@RestController
@RequestMapping("/api/v1/compliance")
@Tag(name = "Compliance", description = "GDPR compliance and data protection operations")
@CrossOrigin(origins = "*")
public class ComplianceController {
    
    private static final Logger logger = LoggerFactory.getLogger(ComplianceController.class);
    
    private final ComplianceApplicationService complianceApplicationService;
    
    public ComplianceController(ComplianceApplicationService complianceApplicationService) {
        this.complianceApplicationService = complianceApplicationService;
    }
    
    @Operation(summary = "Give consent", description = "Records consent for data processing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Consent recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid consent request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/consent")
    @PreAuthorize("hasAuthority('SCOPE_compliance:write')")
    public ResponseEntity<ConsentResponse> giveConsent(
            @Valid @RequestBody GiveConsentRequest request) {
        
        logger.info("Recording consent for data subject: {} in tenant: {}", 
            request.dataSubjectId(), TenantContext.getCurrentTenant());
        
        try {
            ConsentResponse response = complianceApplicationService.giveConsent(request);
            logger.info("Consent recorded successfully with ID: {}", response.consentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error recording consent for data subject: {}", request.dataSubjectId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Withdraw consent", description = "Withdraws previously given consent")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consent withdrawn successfully"),
        @ApiResponse(responseCode = "404", description = "Consent not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/consent/{consentId}")
    @PreAuthorize("hasAuthority('SCOPE_compliance:write')")
    public ResponseEntity<Void> withdrawConsent(
            @Parameter(description = "Consent ID") @PathVariable String consentId) {
        
        logger.info("Withdrawing consent: {}", consentId);
        
        try {
            complianceApplicationService.withdrawConsent(consentId);
            logger.info("Consent withdrawn successfully: {}", consentId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error withdrawing consent: {}", consentId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "Get consents for data subject", description = "Retrieves all consents for a data subject")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consents retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/consent/data-subject/{dataSubjectId}")
    @PreAuthorize("hasAuthority('SCOPE_compliance:read')")
    public ResponseEntity<List<ConsentResponse>> getConsentsForDataSubject(
            @Parameter(description = "Data subject ID") @PathVariable String dataSubjectId) {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Getting consents for data subject: {} in tenant: {}", dataSubjectId, tenantId);
        
        try {
            List<ConsentResponse> consents = complianceApplicationService
                .getConsentsForDataSubject(dataSubjectId, tenantId);
            
            return ResponseEntity.ok(consents);
            
        } catch (Exception e) {
            logger.error("Error getting consents for data subject: {} in tenant: {}", 
                dataSubjectId, tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Check valid consent", description = "Checks if data subject has valid consent for a specific type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consent status retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/consent/check")
    @PreAuthorize("hasAuthority('SCOPE_compliance:read')")
    public ResponseEntity<Boolean> hasValidConsent(
            @Parameter(description = "Data subject ID") @RequestParam String dataSubjectId,
            @Parameter(description = "Consent type") @RequestParam String consentType) {
        
        String tenantId = TenantContext.getCurrentTenant();
        logger.info("Checking valid consent for data subject: {} type: {} in tenant: {}", 
            dataSubjectId, consentType, tenantId);
        
        try {
            boolean hasConsent = complianceApplicationService
                .hasValidConsent(dataSubjectId, tenantId, consentType);
            
            return ResponseEntity.ok(hasConsent);
            
        } catch (Exception e) {
            logger.error("Error checking consent for data subject: {} type: {} in tenant: {}", 
                dataSubjectId, consentType, tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(summary = "Request data portability", description = "Creates a data portability request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Data portability request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/data-portability")
    @PreAuthorize("hasAuthority('SCOPE_compliance:write')")
    public ResponseEntity<String> requestDataPortability(
            @Valid @RequestBody DataPortabilityRequestRequest request) {
        
        logger.info("Creating data portability request for data subject: {} in tenant: {}", 
            request.dataSubjectId(), TenantContext.getCurrentTenant());
        
        try {
            String requestId = complianceApplicationService.requestDataPortability(request);
            logger.info("Data portability request created successfully with ID: {}", requestId);
            return ResponseEntity.status(HttpStatus.CREATED).body(requestId);
            
        } catch (Exception e) {
            logger.error("Error creating data portability request for data subject: {}", 
                request.dataSubjectId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Operation(summary = "Request data deletion", description = "Creates a data deletion request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Data deletion request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping("/data-deletion")
    @PreAuthorize("hasAuthority('SCOPE_compliance:write')")
    public ResponseEntity<String> requestDataDeletion(
            @Valid @RequestBody DataDeletionRequestRequest request) {
        
        logger.info("Creating data deletion request for data subject: {} in tenant: {}", 
            request.dataSubjectId(), TenantContext.getCurrentTenant());
        
        try {
            String requestId = complianceApplicationService.requestDataDeletion(request);
            logger.info("Data deletion request created successfully with ID: {}", requestId);
            return ResponseEntity.status(HttpStatus.CREATED).body(requestId);
            
        } catch (Exception e) {
            logger.error("Error creating data deletion request for data subject: {}", 
                request.dataSubjectId(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}