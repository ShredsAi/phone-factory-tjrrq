package ai.shreds.application.services;

import ai.shreds.domain.ports.DomainOutputPortEmailService;
import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;
import ai.shreds.shared.dtos.SharedOrderLineItemEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Strategy implementation for transmitting purchase orders via email.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationEmailCommunicationStrategy implements ApplicationSupplierCommunicationStrategy {

    private final DomainOutputPortEmailService domainEmailPort;
    
    @Override
    public void transmitOrder(SharedPurchaseOrderDataDTO purchaseOrder) {
        log.info("Transmitting purchase order {} to supplier {} via email", 
                purchaseOrder.getOrderId(), purchaseOrder.getSupplierId());
        
        try {
            // Generate PDF representation of the purchase order
            byte[] orderPdf = generateOrderPdf(purchaseOrder);
            
            // Get supplier email (this would come from a supplier repository in a real implementation)
            String supplierEmail = getSupplierEmail(purchaseOrder.getSupplierId());
            
            if (supplierEmail == null || supplierEmail.isEmpty()) {
                log.error("No email address found for supplier {}", purchaseOrder.getSupplierId());
                throw new RuntimeException("No email address found for supplier " + purchaseOrder.getSupplierId());
            }
            
            // Send email with PDF attachment
            domainEmailPort.sendPurchaseOrderEmail(supplierEmail, orderPdf, purchaseOrder.getOrderId());
            
            log.info("Successfully sent purchase order {} to supplier {} via email: {}", 
                    purchaseOrder.getOrderId(), purchaseOrder.getSupplierId(), supplierEmail);
            
            // Also send notification to internal procurement team
            sendInternalNotification(purchaseOrder);
            
        } catch (Exception e) {
            log.error("Failed to transmit purchase order {} to supplier {} via email", 
                    purchaseOrder.getOrderId(), purchaseOrder.getSupplierId(), e);
            throw new RuntimeException("Failed to transmit purchase order via email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isSupported(String communicationMethod) {
        return "EMAIL".equalsIgnoreCase(communicationMethod) || 
               "MAIL".equalsIgnoreCase(communicationMethod);
    }
    
    /**
     * Generates a PDF representation of the purchase order.
     * 
     * @param purchaseOrder The purchase order to convert to PDF
     * @return PDF content as byte array
     */
    private byte[] generateOrderPdf(SharedPurchaseOrderDataDTO purchaseOrder) {
        // In a real implementation, this would use a PDF library
        // For now, we'll return a simple text representation as bytes
        
        StringBuilder sb = new StringBuilder();
        sb.append("PURCHASE ORDER: ").append(purchaseOrder.getOrderId()).append("\n\n");
        sb.append("Supplier: ").append(purchaseOrder.getSupplierId()).append("\n");
        sb.append("Date: ").append(purchaseOrder.getOrderDate()).append("\n");
        sb.append("Expected Delivery: ").append(purchaseOrder.getExpectedDeliveryDate()).append("\n\n");
        
        sb.append("LINE ITEMS:\n");
        sb.append("---------------------------------------------------\n");
        sb.append(String.format("%-15s %-10s %-15s %-15s\n", "Material", "Quantity", "Unit Price", "Line Total"));
        sb.append("---------------------------------------------------\n");
        
        BigDecimal total = BigDecimal.ZERO;
        for (SharedOrderLineItemEventDTO item : purchaseOrder.getLineItems()) {
            sb.append(String.format("%-15s %-10s %-15s %-15s\n", 
                    item.getMaterialId(),
                    item.getQuantity(),
                    item.getUnitPrice().getAmount() + " " + item.getUnitPrice().getCurrency(),
                    item.getLineTotal().getAmount() + " " + item.getLineTotal().getCurrency()));
            total = total.add(item.getLineTotal().getAmount());
        }
        
        sb.append("---------------------------------------------------\n");
        sb.append(String.format("%-41s %-15s\n", "TOTAL:", 
                total + " " + (purchaseOrder.getLineItems().size() > 0 
                    ? purchaseOrder.getLineItems().get(0).getUnitPrice().getCurrency() : "")));
        sb.append("\n\n");
        
        sb.append("Payment Terms: ").append(purchaseOrder.getPaymentTerms()).append("\n");
        sb.append("Delivery Conditions: ").append(purchaseOrder.getDeliveryConditions()).append("\n");
        
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
    
    /**
     * Gets the email address for a supplier.
     * 
     * @param supplierId The supplier ID
     * @return The email address of the supplier
     */
    private String getSupplierEmail(String supplierId) {
        // In a real implementation, this would query a supplier repository
        // For now, generate a placeholder email based on supplier ID
        return "orders@" + supplierId.toLowerCase().replace("-", "") + ".example.com";
    }
    
    /**
     * Sends an internal notification that a purchase order has been sent via email.
     * 
     * @param purchaseOrder The purchase order that was sent
     */
    private void sendInternalNotification(SharedPurchaseOrderDataDTO purchaseOrder) {
        try {
            String subject = "Purchase Order " + purchaseOrder.getOrderId() + " sent to " + 
                    purchaseOrder.getSupplierId();
            
            String body = "Purchase order " + purchaseOrder.getOrderId() + " was sent to supplier " + 
                    purchaseOrder.getSupplierId() + " via email on " + 
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".\n\n" +
                    "Please follow up if no acknowledgment is received within 48 hours.";
            
            // Send to procurement team email
            domainEmailPort.sendNotificationEmail("procurement@company.com", subject, body);
            
        } catch (Exception e) {
            // Log but don't fail the overall process
            log.error("Failed to send internal notification for purchase order {}", 
                    purchaseOrder.getOrderId(), e);
        }
    }
}