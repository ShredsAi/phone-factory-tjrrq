package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationReorderInputPort;
import ai.shreds.shared.dtos.SharedReorderRequestEventDTO;
import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;
import ai.shreds.shared.dtos.SharedSupplierValidationRequestDTO;
import ai.shreds.shared.dtos.SharedSupplierValidationResponseDTO;
import ai.shreds.application.exceptions.ApplicationSupplierValidationException;
import ai.shreds.application.exceptions.ApplicationOrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationPurchaseOrderService implements ApplicationReorderInputPort {

    private final ApplicationPurchaseOrderFactory purchaseOrderFactory;
    private final ApplicationPriceValidator priceValidator;
    private final ApplicationDeliveryDateCalculator deliveryDateCalculator;
    private final ApplicationWorkflowCoordinator workflowCoordinator;
    private final ApplicationEventService eventService;

    @Override
    @Transactional
    public void processReorderRequest(SharedReorderRequestEventDTO reorderData) {
        log.info("Processing reorder request for material: {}", reorderData.getMaterialId());
        
        try {
            // Validate supplier capability
            validateSupplierCapability(reorderData.getMaterialId(), reorderData.getReorderQuantity());
            
            // Create purchase order
            String orderId = createPurchaseOrder(reorderData);
            
            log.info("Successfully created purchase order: {}", orderId);
            
        } catch (Exception e) {
            log.error("Failed to process reorder request for material: {}", reorderData.getMaterialId(), e);
            throw e;
        }
    }

    private void validateSupplierCapability(String materialId, BigDecimal quantity) {
        // This will be implemented when domain layer is available
        // For now, basic validation
        if (materialId == null || materialId.trim().isEmpty()) {
            throw new ApplicationSupplierValidationException("Material ID cannot be null or empty");
        }
        
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationSupplierValidationException("Quantity must be positive");
        }
        
        log.debug("Supplier capability validation completed for material: {}", materialId);
    }

    private String createPurchaseOrder(SharedReorderRequestEventDTO reorderData) {
        // Generate unique order ID
        String orderId = generateOrderId();
        
        // Create purchase order through factory
        SharedPurchaseOrderDataDTO orderData = purchaseOrderFactory.createPurchaseOrder(reorderData);
        
        // Calculate expected delivery date
        LocalDateTime expectedDelivery = deliveryDateCalculator.calculateExpectedDelivery(
            7, // Default supplier lead time
            reorderData.getPriority()
        );
        
        // Set calculated delivery date
        orderData.setExpectedDeliveryDate(expectedDelivery.toString());
        
        // Initiate workflow
        workflowCoordinator.initiateWorkflow(orderId, orderData.getTotalAmount().getAmount());
        
        // Publish order created event
        eventService.publishPurchaseOrderCreated(mapToCreatedEvent(orderData));
        
        return orderId;
    }

    private String generateOrderId() {
        return "PO-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ai.shreds.shared.dtos.SharedPurchaseOrderCreatedEventDTO mapToCreatedEvent(SharedPurchaseOrderDataDTO orderData) {
        ai.shreds.shared.dtos.SharedPurchaseOrderCreatedEventDTO event = new ai.shreds.shared.dtos.SharedPurchaseOrderCreatedEventDTO();
        event.setEventType("PurchaseOrderCreated");
        event.setOrderId(orderData.getOrderId());
        event.setSupplierId(orderData.getSupplierId());
        event.setTotalAmount(orderData.getTotalAmount());
        event.setExpectedDeliveryDate(orderData.getExpectedDeliveryDate());
        event.setLineItems(orderData.getLineItems());
        return event;
    }
}