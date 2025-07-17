package ai.shreds.domain.ports;

/**
 * Domain output port for email service operations.
 * This port is implemented by infrastructure clients that connect to email services.
 */
public interface DomainOutputPortEmailService {

    /**
     * Sends a purchase order as a PDF attachment to a supplier via email.
     *
     * @param recipientEmail the email address of the recipient (supplier)
     * @param orderPdf the PDF document as a byte array
     * @param orderId the ID of the purchase order (for reference)
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if email sending fails
     */
    void sendPurchaseOrderEmail(String recipientEmail, byte[] orderPdf, String orderId);

    /**
     * Sends a notification email with custom subject and body.
     *
     * @param recipientEmail the email address of the recipient
     * @param subject the email subject
     * @param body the email body content
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if email sending fails
     */
    void sendNotificationEmail(String recipientEmail, String subject, String body);
}