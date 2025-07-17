package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainInvalidOrderStateException;
import ai.shreds.domain.value_objects.*;
import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;
import ai.shreds.shared.dtos.SharedOrderLineItemEventDTO;
import ai.shreds.shared.dtos.SharedMonetaryAmountDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DomainPurchaseOrderAggregate {
    private final DomainPurchaseOrderId orderId;
    private final DomainSupplierId supplierId;
    private final LocalDateTime orderDate;
    private DomainOrderStatus status;
    private final List<DomainOrderLineItem> lineItems;
    private DomainMonetaryAmount totalAmount;
    private LocalDateTime expectedDeliveryDate;
    private String paymentTerms;
    private String deliveryConditions;
    private String contractualObligations;

    public DomainPurchaseOrderAggregate(DomainPurchaseOrderId orderId,
                                       DomainSupplierId supplierId,
                                       LocalDateTime orderDate,
                                       DomainOrderStatus status,
                                       List<DomainOrderLineItem> lineItems,
                                       DomainMonetaryAmount totalAmount,
                                       LocalDateTime expectedDeliveryDate,
                                       String paymentTerms,
                                       String deliveryConditions,
                                       String contractualObligations) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        if (supplierId == null) {
            throw new IllegalArgumentException("supplierId cannot be null");
        }
        if (orderDate == null) {
            throw new IllegalArgumentException("orderDate cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }
        if (lineItems == null) {
            throw new IllegalArgumentException("lineItems cannot be null");
        }
        if (totalAmount == null) {
            throw new IllegalArgumentException("totalAmount cannot be null");
        }
        
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.status = status;
        this.lineItems = new ArrayList<>(lineItems);
        this.totalAmount = totalAmount;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.paymentTerms = paymentTerms;
        this.deliveryConditions = deliveryConditions;
        this.contractualObligations = contractualObligations;
    }

    public void addLineItem(DomainMaterialId materialId, java.math.BigDecimal quantity, DomainMonetaryAmount unitPrice) {
        if (status != DomainOrderStatus.DRAFT) {
            throw new DomainInvalidOrderStateException(status.name(), "addLineItem");
        }
        DomainOrderLineItem lineItem = new DomainOrderLineItem(
                UUID.randomUUID(),
                materialId,
                quantity,
                unitPrice,
                expectedDeliveryDate,
                null
        );
        lineItems.add(lineItem);
        calculateTotalAmount();
    }

    public void removeLineItem(UUID lineItemId) {
        if (status != DomainOrderStatus.DRAFT) {
            throw new DomainInvalidOrderStateException(status.name(), "removeLineItem");
        }
        lineItems.removeIf(item -> item.getLineItemId().equals(lineItemId));
        calculateTotalAmount();
    }

    public DomainMonetaryAmount calculateTotalAmount() {
        if (lineItems.isEmpty()) {
            totalAmount = new DomainMonetaryAmount(java.math.BigDecimal.ZERO, "USD");
            return totalAmount;
        }
        DomainMonetaryAmount total = lineItems.get(0).calculateLineTotal();
        for (int i = 1; i < lineItems.size(); i++) {
            total = total.add(lineItems.get(i).calculateLineTotal());
        }
        totalAmount = total;
        return total;
    }

    public void submitForApproval() {
        if (status != DomainOrderStatus.DRAFT) {
            throw new DomainInvalidOrderStateException(status.name(), "submitForApproval");
        }
        if (lineItems.isEmpty()) {
            throw new IllegalStateException("Cannot submit empty purchase order for approval");
        }
        status = DomainOrderStatus.PENDING_APPROVAL;
    }

    public void approve() {
        if (status != DomainOrderStatus.PENDING_APPROVAL) {
            throw new DomainInvalidOrderStateException(status.name(), "approve");
        }
        status = DomainOrderStatus.APPROVED;
    }

    public void reject(String reason) {
        if (status != DomainOrderStatus.PENDING_APPROVAL) {
            throw new DomainInvalidOrderStateException(status.name(), "reject");
        }
        status = DomainOrderStatus.DRAFT;
        // Could log rejection reason here
    }

    public void sendToSupplier() {
        if (status != DomainOrderStatus.APPROVED) {
            throw new DomainInvalidOrderStateException(status.name(), "sendToSupplier");
        }
        status = DomainOrderStatus.SENT;
    }

    public void acknowledge(String supplierId, LocalDateTime deliveryDate) {
        if (status != DomainOrderStatus.SENT) {
            throw new DomainInvalidOrderStateException(status.name(), "acknowledge");
        }
        if (!this.supplierId.getValue().equals(supplierId)) {
            throw new IllegalArgumentException("Invalid supplier acknowledgment");
        }
        status = DomainOrderStatus.ACKNOWLEDGED;
        expectedDeliveryDate = deliveryDate;
    }

    public SharedPurchaseOrderDataDTO toSharedDTO() {
        SharedPurchaseOrderDataDTO dto = new SharedPurchaseOrderDataDTO();
        dto.setOrderId(orderId.getValue());
        dto.setSupplierId(supplierId.getValue());
        dto.setOrderDate(orderDate.toString());
        dto.setStatus(status.name());
        
        List<SharedOrderLineItemEventDTO> lineItemDTOs = new ArrayList<>();
        for (DomainOrderLineItem item : lineItems) {
            SharedOrderLineItemEventDTO lineItemDTO = new SharedOrderLineItemEventDTO();
            lineItemDTO.setLineItemId(item.getLineItemId().toString());
            lineItemDTO.setMaterialId(item.getMaterialId().getValue());
            lineItemDTO.setQuantity(item.getQuantity());
            
            SharedMonetaryAmountDTO unitPriceDTO = item.getUnitPrice().toSharedDTO();
            lineItemDTO.setUnitPrice(unitPriceDTO);
            
            SharedMonetaryAmountDTO lineTotalDTO = item.calculateLineTotal().toSharedDTO();
            lineItemDTO.setLineTotal(lineTotalDTO);
            
            if (item.getRequestedDeliveryDate() != null) {
                lineItemDTO.setRequestedDeliveryDate(item.getRequestedDeliveryDate().toString());
            }
            
            lineItemDTOs.add(lineItemDTO);
        }
        dto.setLineItems(lineItemDTOs);
        
        SharedMonetaryAmountDTO totalAmountDTO = totalAmount.toSharedDTO();
        dto.setTotalAmount(totalAmountDTO);
        
        if (expectedDeliveryDate != null) {
            dto.setExpectedDeliveryDate(expectedDeliveryDate.toString());
        }
        
        dto.setPaymentTerms(paymentTerms);
        dto.setDeliveryConditions(deliveryConditions);
        
        return dto;
    }

    // Getters
    public DomainPurchaseOrderId getOrderId() { return orderId; }
    public DomainSupplierId getSupplierId() { return supplierId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public DomainOrderStatus getStatus() { return status; }
    public List<DomainOrderLineItem> getLineItems() { return new ArrayList<>(lineItems); }
    public DomainMonetaryAmount getTotalAmount() { return totalAmount; }
    public LocalDateTime getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public String getPaymentTerms() { return paymentTerms; }
    public String getDeliveryConditions() { return deliveryConditions; }
    public String getContractualObligations() { return contractualObligations; }
}