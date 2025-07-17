package ai.shreds.application.services;

import ai.shreds.domain.ports.DomainOutputPortSupplierApi;
import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for transmitting purchase orders via supplier REST APIs.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationApiCommunicationStrategy implements ApplicationSupplierCommunicationStrategy {

    private final DomainOutputPortSupplierApi domainSupplierApiPort;
    
    @Override
    public void transmitOrder(SharedPurchaseOrderDataDTO purchaseOrder) {
        log.info("Transmitting purchase order {} to supplier {} via API", 
                purchaseOrder.getOrderId(), purchaseOrder.getSupplierId());
        
        try {
            // Call supplier API through domain port
            String transactionId = domainSupplierApiPort.transmitPurchaseOrder(
                    purchaseOrder.getSupplierId(), purchaseOrder);
            
            if (transactionId != null && !transactionId.isEmpty()) {
                log.info("Successfully transmitted purchase order {} to supplier {}, transaction ID: {}", 
                        purchaseOrder.getOrderId(), purchaseOrder.getSupplierId(), transactionId);
            } else {
                log.warn("Transmission completed but no transaction ID returned for purchase order {} to supplier {}", 
                        purchaseOrder.getOrderId(), purchaseOrder.getSupplierId());
                throw new RuntimeException("Failed to get transaction ID from supplier API");
            }
            
        } catch (Exception e) {
            log.error("Failed to transmit purchase order {} to supplier {} via API", 
                    purchaseOrder.getOrderId(), purchaseOrder.getSupplierId(), e);
            throw new RuntimeException("Failed to transmit purchase order via API: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isSupported(String communicationMethod) {
        return "API".equalsIgnoreCase(communicationMethod) || 
               "REST".equalsIgnoreCase(communicationMethod);
    }
}