package ai.shreds.application.services;

import ai.shreds.domain.ports.DomainOutputPortEdiTransmission;
import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for transmitting purchase orders via EDI (Electronic Data Interchange).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationEdiCommunicationStrategy implements ApplicationSupplierCommunicationStrategy {

    private final DomainOutputPortEdiTransmission domainEdiPort;
    
    @Override
    public void transmitOrder(SharedPurchaseOrderDataDTO purchaseOrder) {
        log.info("Transmitting purchase order {} to supplier {} via EDI", 
                purchaseOrder.getOrderId(), purchaseOrder.getSupplierId());
        
        try {
            // Send order via EDI through domain port
            String transactionId = domainEdiPort.sendEdiOrder(
                    purchaseOrder.getSupplierId(), purchaseOrder);
            
            if (transactionId != null && !transactionId.isEmpty()) {
                log.info("Successfully transmitted purchase order {} to supplier {} via EDI, transaction ID: {}", 
                        purchaseOrder.getOrderId(), purchaseOrder.getSupplierId(), transactionId);
                
                // For EDI, we should also check for acknowledgment
                checkEdiAcknowledgment(transactionId, purchaseOrder);
            } else {
                log.warn("EDI transmission completed but no transaction ID returned for purchase order {} to supplier {}", 
                        purchaseOrder.getOrderId(), purchaseOrder.getSupplierId());
                throw new RuntimeException("Failed to get transaction ID from EDI gateway");
            }
            
        } catch (Exception e) {
            log.error("Failed to transmit purchase order {} to supplier {} via EDI", 
                    purchaseOrder.getOrderId(), purchaseOrder.getSupplierId(), e);
            throw new RuntimeException("Failed to transmit purchase order via EDI: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isSupported(String communicationMethod) {
        return "EDI".equalsIgnoreCase(communicationMethod);
    }
    
    /**
     * Checks for EDI acknowledgment for a given transaction.
     * 
     * @param transactionId The EDI transaction ID
     * @param purchaseOrder The original purchase order
     */
    private void checkEdiAcknowledgment(String transactionId, SharedPurchaseOrderDataDTO purchaseOrder) {
        try {
            // This might be implemented as an asynchronous process in a real system
            // For now, we'll do a synchronous check
            String acknowledgmentStatus = domainEdiPort.receiveEdiAcknowledgment(transactionId);
            
            if (acknowledgmentStatus != null && !acknowledgmentStatus.isEmpty()) {
                log.info("Received EDI acknowledgment for purchase order {}: {}", 
                        purchaseOrder.getOrderId(), acknowledgmentStatus);
            } else {
                log.warn("No immediate EDI acknowledgment received for purchase order {}", 
                        purchaseOrder.getOrderId());
                // In a real system, we might schedule a follow-up check
            }
            
        } catch (Exception e) {
            log.error("Error checking EDI acknowledgment for purchase order {}", 
                    purchaseOrder.getOrderId(), e);
            // We don't fail the overall transmission for an acknowledgment check failure
        }
    }
}