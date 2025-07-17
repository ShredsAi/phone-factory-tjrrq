package ai.shreds.domain.ports;

import ai.shreds.domain.value_objects.DomainMonetaryAmount;

/**
 * Domain output port for notification service interactions.
 * Implemented by infrastructure clients to send notifications to stakeholders.
 */
public interface DomainOutputPortNotification {

    /**
     * Sends a notification to an approver that a purchase order is awaiting their action.
     *
     * @param recipientId the ID of the recipient (approver)
     * @param orderId     the ID of the purchase order
     * @param orderValue  the monetary value of the order
     */
    void sendApprovalNotification(String recipientId, String orderId, DomainMonetaryAmount orderValue);

    /**
     * Sends a notification to a stakeholder about a change in order status.
     *
     * @param recipientId the ID of the recipient
     * @param orderId     the ID of the purchase order
     * @param newStatus   the new status of the order
     */
    void sendOrderStatusNotification(String recipientId, String orderId, String newStatus);
}