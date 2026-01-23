package com.xavier.mozdeliveryapi.payment.application.usecase;
import com.xavier.mozdeliveryapi.shared.domain.valueobject.RefundStatus;
import com.xavier.mozdeliveryapi.payment.domain.valueobject.RefundId;


/**
 * Domain service for refund notifications.
 */
public interface RefundNotificationService {
    
    /**
     * Send refund created notification.
     */
    void sendRefundCreatedNotification(RefundId refundId);
    
    /**
     * Send refund status change notification.
     */
    void sendRefundStatusChangeNotification(RefundId refundId, RefundStatus oldStatus, RefundStatus newStatus);
    
    /**
     * Send refund completed notification.
     */
    void sendRefundCompletedNotification(RefundId refundId);
    
    /**
     * Send refund failed notification.
     */
    void sendRefundFailedNotification(RefundId refundId, String reason);
    
    /**
     * Send manual approval required notification.
     */
    void sendManualApprovalRequiredNotification(RefundId refundId);
}