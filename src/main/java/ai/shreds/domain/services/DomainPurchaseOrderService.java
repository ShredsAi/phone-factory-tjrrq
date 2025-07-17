package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainApprovalWorkflowEntity;
import ai.shreds.domain.entities.DomainPurchaseOrderAggregate;
import ai.shreds.domain.exceptions.DomainOrderCreationException;
import ai.shreds.domain.exceptions.DomainSupplierNotActiveException;
import ai.shreds.domain.ports.DomainInputPortCreatePurchaseOrder;
import ai.shreds.domain.ports.DomainOutputPortNotification;
import ai.shreds.domain.ports.DomainOutputPortPurchaseOrderRepository;
import ai.shreds.domain.ports.DomainOutputPortSupplierCatalog;
import ai.shreds.domain.ports.DomainOutputPortSupplierValidation;
import ai.shreds.domain.ports.DomainOutputPortWorkflowRepository;
import ai.shreds.domain.value_objects.*;
import ai.shreds.shared.dtos.SharedOrderLineItemEventDTO;
import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;
import ai.shreds.shared.dtos.SharedSupplierInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain service for creating and managing purchase orders.
 * Implements the DomainInputPortCreatePurchaseOrder interface.
 */
public class DomainPurchaseOrderService implements DomainInputPortCreatePurchaseOrder {

    private final DomainOutputPortPurchaseOrderRepository purchaseOrderRepository;
    private final DomainOutputPortSupplierValidation supplierValidation;
    private final DomainOutputPortSupplierCatalog supplierCatalog;
    private final DomainOutputPortWorkflowRepository workflowRepository;
    private final DomainOutputPortNotification notificationPort;

    /**
     * Creates a new DomainPurchaseOrderService with the required dependencies.
     * 
     * @param purchaseOrderRepository repository for persisting purchase orders
     * @param supplierValidation validation service for supplier capabilities
     * @param supplierCatalog catalog service for supplier product information
     * @param workflowRepository repository for approval workflows
     * @param notificationPort notification service for sending alerts
     */
    public DomainPurchaseOrderService(DomainOutputPortPurchaseOrderRepository purchaseOrderRepository,
                                      DomainOutputPortSupplierValidation supplierValidation,
                                      DomainOutputPortSupplierCatalog supplierCatalog,
                                      DomainOutputPortWorkflowRepository workflowRepository,
                                      DomainOutputPortNotification notificationPort) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierValidation = supplierValidation;
        this.supplierCatalog = supplierCatalog;
        this.workflowRepository = workflowRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public DomainPurchaseOrderId createOrder(SharedPurchaseOrderDataDTO orderData) {
        try {
            // Validate the supplier status
            validateSupplierStatus(orderData.getSupplierId());
            
            // Create purchase order ID or use provided one
            DomainPurchaseOrderId orderId;
            if (orderData.getOrderId() == null || orderData.getOrderId().isEmpty()) {
                orderId = new DomainPurchaseOrderId(generateOrderId());
            } else {
                orderId = new DomainPurchaseOrderId(orderData.getOrderId());
            }
            
            // Parse order date or use current date
            LocalDateTime orderDate;
            if (orderData.getOrderDate() == null || orderData.getOrderDate().isEmpty()) {
                orderDate = LocalDateTime.now();
            } else {
                orderDate = LocalDateTime.parse(orderData.getOrderDate());
            }
            
            // Parse expected delivery date
            LocalDateTime expectedDeliveryDate = null;
            if (orderData.getExpectedDeliveryDate() != null && !orderData.getExpectedDeliveryDate().isEmpty()) {
                expectedDeliveryDate = LocalDateTime.parse(orderData.getExpectedDeliveryDate());
            }
            
            // Create line items
            List<DomainOrderLineItem> lineItems = new ArrayList<>();
            for (SharedOrderLineItemEventDTO lineItemDto : orderData.getLineItems()) {
                DomainMaterialId materialId = new DomainMaterialId(lineItemDto.getMaterialId());
                BigDecimal quantity = lineItemDto.getQuantity();
                DomainMonetaryAmount unitPrice = new DomainMonetaryAmount(
                    lineItemDto.getUnitPrice().getAmount(),
                    lineItemDto.getUnitPrice().getCurrency()
                );
                
                // Validate that supplier can fulfill this material and quantity
                validateSupplierCapability(orderData.getSupplierId(), lineItemDto.getMaterialId(), quantity);
                
                LocalDateTime itemDeliveryDate = null;
                if (lineItemDto.getRequestedDeliveryDate() != null && !lineItemDto.getRequestedDeliveryDate().isEmpty()) {
                    itemDeliveryDate = LocalDateTime.parse(lineItemDto.getRequestedDeliveryDate());
                }
                
                DomainOrderLineItem lineItem = new DomainOrderLineItem(
                    UUID.randomUUID(),
                    materialId,
                    quantity,
                    unitPrice,
                    itemDeliveryDate,
                    null
                );
                lineItems.add(lineItem);
            }
            
            // Calculate total amount
            DomainMonetaryAmount totalAmount = new DomainMonetaryAmount(BigDecimal.ZERO, "USD");
            if (!lineItems.isEmpty()) {
                totalAmount = lineItems.get(0).calculateLineTotal();
                for (int i = 1; i < lineItems.size(); i++) {
                    totalAmount = totalAmount.add(lineItems.get(i).calculateLineTotal());
                }
            }
            
            // Create the purchase order aggregate using the correct constructor
            DomainPurchaseOrderAggregate order = new DomainPurchaseOrderAggregate(
                orderId,
                new DomainSupplierId(orderData.getSupplierId()),
                orderDate,
                DomainOrderStatus.DRAFT,
                lineItems,
                totalAmount,
                expectedDeliveryDate,
                orderData.getPaymentTerms(),
                orderData.getDeliveryConditions(),
                null // contractual obligations, not provided in the DTO
            );
            
            // Save the order
            DomainPurchaseOrderAggregate savedOrder = purchaseOrderRepository.save(order);
            
            // Create a workflow for this order
            createWorkflow(savedOrder.getOrderId().getValue(), savedOrder.getTotalAmount());
            
            return savedOrder.getOrderId();
        } catch (Exception e) {
            throw new DomainOrderCreationException("Failed to create purchase order", e);
        }
    }

    /**
     * Validates that a supplier is active and able to accept orders.
     * 
     * @param supplierId the ID of the supplier to validate
     * @throws DomainSupplierNotActiveException if the supplier is not active
     */
    private void validateSupplierStatus(String supplierId) {
        SharedSupplierInfoDTO supplierInfo = supplierValidation.getSupplierInfo(supplierId);
        if (supplierInfo == null) {
            throw new DomainSupplierNotActiveException("Supplier not found: " + supplierId);
        }
        if (!supplierInfo.getIsActive()) {
            throw new DomainSupplierNotActiveException(supplierId);
        }
    }

    /**
     * Validates that a supplier can fulfill an order for a specific material and quantity.
     * 
     * @param supplierId the ID of the supplier
     * @param materialId the ID of the material
     * @param quantity the quantity being ordered
     * @throws DomainOrderCreationException if validation fails
     */
    private void validateSupplierCapability(String supplierId, String materialId, BigDecimal quantity) {
        boolean isCapable = supplierValidation.validateSupplierCapability(supplierId, materialId, quantity);
        if (!isCapable) {
            throw new DomainOrderCreationException(
                "Supplier " + supplierId + " cannot fulfill order for material " + materialId + 
                    " with quantity " + quantity
            );
        }
    }

    /**
     * Creates a new approval workflow for a purchase order based on its value.
     * 
     * @param orderId the ID of the purchase order
     * @param orderValue the monetary value of the order
     * @return the ID of the created workflow
     */
    private String createWorkflow(String orderId, DomainMonetaryAmount orderValue) {
        DomainApprovalLevel requiredLevel = determineRequiredApprovalLevel(orderValue);
        
        DomainApprovalWorkflowEntity workflow = new DomainApprovalWorkflowEntity(
            UUID.randomUUID(),
            new DomainPurchaseOrderId(orderId),
            requiredLevel
        );
        
        // Determine the first approver based on the level
        String firstApproverId = determineNextApprover(DomainApprovalLevel.SUPERVISOR.name());
        workflow.initiateWorkflow(DomainApprovalLevel.SUPERVISOR, firstApproverId);
        
        DomainApprovalWorkflowEntity savedWorkflow = workflowRepository.save(workflow);
        
        // Notify the first approver
        notificationPort.sendApprovalNotification(firstApproverId, orderId, orderValue);
        
        return savedWorkflow.getWorkflowId().toString();
    }

    /**
     * Determines the required approval level based on order value.
     * 
     * @param orderValue the monetary value of the order
     * @return the required approval level
     */
    private DomainApprovalLevel determineRequiredApprovalLevel(DomainMonetaryAmount orderValue) {
        BigDecimal amount = orderValue.getAmount();
        BigDecimal level1Threshold = new BigDecimal("5000.00");
        BigDecimal level2Threshold = new BigDecimal("25000.00");
        
        if (amount.compareTo(level2Threshold) > 0) {
            return DomainApprovalLevel.DIRECTOR;
        } else if (amount.compareTo(level1Threshold) > 0) {
            return DomainApprovalLevel.MANAGER;
        } else {
            return DomainApprovalLevel.SUPERVISOR;
        }
    }

    /**
     * Determines the next approver for a given approval level.
     * In a real implementation, this would likely involve a lookup in a user repository.
     * 
     * @param currentLevel the current approval level
     * @return the ID of the next approver
     */
    private String determineNextApprover(String currentLevel) {
        // This is a simplified implementation
        // In a real system, this would query a user/role repository to find appropriate approvers
        switch (currentLevel) {
            case "SUPERVISOR":
                return "SUP-12345";
            case "MANAGER":
                return "MGR-67890";
            case "DIRECTOR":
                return "DIR-54321";
            default:
                return "SUP-12345";
        }
    }

    /**
     * Generates a unique purchase order ID.
     * 
     * @return a formatted purchase order ID
     */
    private String generateOrderId() {
        int year = LocalDateTime.now().getYear();
        int randomNumber = 100 + (int) (Math.random() * 900);  // 3-digit random number
        return String.format("PO-%04d-%03d", year, randomNumber);
    }
}