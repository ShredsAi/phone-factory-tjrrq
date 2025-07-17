package ai.shreds.infrastructure.repositories;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
public class InfrastructurePurchaseOrderJpaEntity {
    
    @Id
    @Column(name = "order_id", length = 50)
    private String orderId;
    
    @Column(name = "supplier_id", length = 50, nullable = false)
    private String supplierId;
    
    @Column(name = "order_date", nullable = false)
    private Timestamp orderDate;
    
    @Column(name = "status", length = 30, nullable = false)
    private String status;
    
    @Column(name = "total_amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;
    
    @Column(name = "expected_delivery_date")
    private Timestamp expectedDeliveryDate;
    
    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;
    
    @Column(name = "delivery_conditions", columnDefinition = "TEXT")
    private String deliveryConditions;
    
    @Column(name = "contractual_obligations", columnDefinition = "TEXT")
    private String contractualObligations;
    
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InfrastructureOrderLineItemJpaEntity> lineItems = new ArrayList<>();
    
    // Default constructor
    public InfrastructurePurchaseOrderJpaEntity() {}
    
    // Constructor with required fields
    public InfrastructurePurchaseOrderJpaEntity(String orderId, String supplierId, Timestamp orderDate, 
                                               String status, BigDecimal totalAmount, String currency) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }
    
    // Getters and setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    public Timestamp getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Timestamp getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    
    public void setExpectedDeliveryDate(Timestamp expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    
    public String getPaymentTerms() {
        return paymentTerms;
    }
    
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }
    
    public String getDeliveryConditions() {
        return deliveryConditions;
    }
    
    public void setDeliveryConditions(String deliveryConditions) {
        this.deliveryConditions = deliveryConditions;
    }
    
    public String getContractualObligations() {
        return contractualObligations;
    }
    
    public void setContractualObligations(String contractualObligations) {
        this.contractualObligations = contractualObligations;
    }
    
    public List<InfrastructureOrderLineItemJpaEntity> getLineItems() {
        return lineItems;
    }
    
    public void setLineItems(List<InfrastructureOrderLineItemJpaEntity> lineItems) {
        this.lineItems = lineItems;
    }
    
    public void addLineItem(InfrastructureOrderLineItemJpaEntity lineItem) {
        lineItems.add(lineItem);
        lineItem.setPurchaseOrder(this);
    }
    
    public void removeLineItem(InfrastructureOrderLineItemJpaEntity lineItem) {
        lineItems.remove(lineItem);
        lineItem.setPurchaseOrder(null);
    }
}