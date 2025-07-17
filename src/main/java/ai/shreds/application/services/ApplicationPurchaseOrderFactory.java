package ai.shreds.application.services;

import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;
import ai.shreds.shared.dtos.SharedReorderRequestEventDTO;
import ai.shreds.shared.dtos.SharedOrderLineItemEventDTO;
import ai.shreds.shared.dtos.SharedMonetaryAmountDTO;
import ai.shreds.application.exceptions.ApplicationSupplierValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Factory for creating purchase orders from reorder requests.
 */
@Component
@Slf4j
public class ApplicationPurchaseOrderFactory {

    private static final String DEFAULT_CURRENCY = "USD";
    private static final String DEFAULT_PAYMENT_TERMS = "Net 30";
    private static final String DEFAULT_DELIVERY_CONDITIONS = "Standard Delivery";
    
    /**
     * Creates a purchase order from a reorder request event.
     * 
     * @param reorderRequest The reorder request containing material and quantity information
     * @return A complete purchase order data object
     */
    public SharedPurchaseOrderDataDTO createPurchaseOrder(SharedReorderRequestEventDTO reorderRequest) {
        validateOrderData(reorderRequest);
        
        String orderId = generateOrderId();
        
        // Get supplier ID (would be selected based on material in a real implementation)
        String supplierId = "SUP-" + (Math.abs(reorderRequest.getMaterialId().hashCode()) % 1000);
        
        // Create line item
        SharedOrderLineItemEventDTO lineItem = createLineItem(reorderRequest);
        List<SharedOrderLineItemEventDTO> lineItems = new ArrayList<>();
        lineItems.add(lineItem);
        
        // Calculate total amount
        SharedMonetaryAmountDTO totalAmount = new SharedMonetaryAmountDTO();
        totalAmount.setAmount(lineItem.getLineTotal().getAmount());
        totalAmount.setCurrency(lineItem.getLineTotal().getCurrency());
        
        // Create purchase order DTO
        SharedPurchaseOrderDataDTO purchaseOrder = new SharedPurchaseOrderDataDTO();
        purchaseOrder.setOrderId(orderId);
        purchaseOrder.setSupplierId(supplierId);
        purchaseOrder.setOrderDate(LocalDateTime.now().toString());
        purchaseOrder.setStatus("DRAFT");
        purchaseOrder.setLineItems(lineItems);
        purchaseOrder.setTotalAmount(totalAmount);
        purchaseOrder.setExpectedDeliveryDate(reorderRequest.getRequestedDeliveryDate());
        purchaseOrder.setPaymentTerms(DEFAULT_PAYMENT_TERMS);
        purchaseOrder.setDeliveryConditions(DEFAULT_DELIVERY_CONDITIONS);
        
        log.info("Created purchase order {} for material {}", orderId, reorderRequest.getMaterialId());
        
        return purchaseOrder;
    }
    
    /**
     * Generates a unique order ID.
     * 
     * @return A unique order ID string
     */
    private String generateOrderId() {
        return "PO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" 
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Validates the reorder request data.
     * 
     * @param reorderRequest The reorder request to validate
     * @throws ApplicationSupplierValidationException If validation fails
     */
    private void validateOrderData(SharedReorderRequestEventDTO reorderRequest) {
        if (reorderRequest == null) {
            throw new ApplicationSupplierValidationException("Reorder request cannot be null");
        }
        
        if (reorderRequest.getMaterialId() == null || reorderRequest.getMaterialId().trim().isEmpty()) {
            throw new ApplicationSupplierValidationException("Material ID is required");
        }
        
        if (reorderRequest.getReorderQuantity() == null || reorderRequest.getReorderQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationSupplierValidationException("Reorder quantity must be a positive number");
        }
    }
    
    /**
     * Creates a line item from reorder request data.
     * 
     * @param reorderRequest The reorder request data
     * @return A complete line item DTO
     */
    private SharedOrderLineItemEventDTO createLineItem(SharedReorderRequestEventDTO reorderRequest) {
        SharedOrderLineItemEventDTO lineItem = new SharedOrderLineItemEventDTO();
        
        // Generate line item ID
        lineItem.setLineItemId(UUID.randomUUID().toString());
        lineItem.setMaterialId(reorderRequest.getMaterialId());
        lineItem.setQuantity(reorderRequest.getReorderQuantity());
        
        // Set unit price (would be retrieved from supplier catalog in a real implementation)
        BigDecimal unitPrice = BigDecimal.valueOf(10.0); // Default price
        SharedMonetaryAmountDTO unitPriceDTO = new SharedMonetaryAmountDTO();
        unitPriceDTO.setAmount(unitPrice);
        unitPriceDTO.setCurrency(DEFAULT_CURRENCY);
        lineItem.setUnitPrice(unitPriceDTO);
        
        // Calculate line total
        BigDecimal lineTotal = unitPrice.multiply(reorderRequest.getReorderQuantity());
        SharedMonetaryAmountDTO lineTotalDTO = new SharedMonetaryAmountDTO();
        lineTotalDTO.setAmount(lineTotal);
        lineTotalDTO.setCurrency(DEFAULT_CURRENCY);
        lineItem.setLineTotal(lineTotalDTO);
        
        // Set requested delivery date
        lineItem.setRequestedDeliveryDate(reorderRequest.getRequestedDeliveryDate());
        
        return lineItem;
    }
}