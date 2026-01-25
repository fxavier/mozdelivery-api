package com.xavier.mozdeliveryapi.dispatch.application.dto;

import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.CourierApprovalStatus;
import com.xavier.mozdeliveryapi.dispatch.domain.valueobject.DeliveryPersonId;

import jakarta.validation.constraints.NotNull;

/**
 * Request to approve or reject a courier registration.
 */
public record CourierApprovalRequest(
    @NotNull(message = "Delivery person ID is required")
    DeliveryPersonId deliveryPersonId,
    
    @NotNull(message = "Approval status is required")
    CourierApprovalStatus approvalStatus,
    
    String reviewNotes,
    
    String reviewerComments
) {
    
    public CourierApprovalRequest {
        if (approvalStatus != null && 
            approvalStatus != CourierApprovalStatus.APPROVED && 
            approvalStatus != CourierApprovalStatus.REJECTED) {
            throw new IllegalArgumentException("Approval status must be APPROVED or REJECTED");
        }
    }
}