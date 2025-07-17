package ai.shreds.infrastructure.repositories;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "order_line_items")
public class InfrastructureOrderLineItemJpaEntity {
    
    @Id
    @Column(name = "line_item_id")
    private UUID lineItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private InfrastructurePurchaseOrderJpaEntity purchaseOrder;
    
    @Column(name = "material_id", length = 50, nullable = false)
    private String materialId;
    
    @Column(name = "quantity", precision = 18, scale = 4, nullable = false)
    private BigDecimal quantity;
    
    @Column(name = "unit_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal unitPrice;
    
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;
    
    @Column(name = "line_total", precision = 19, scale = 4, nullable = false)
    private BigDecimal lineTotal;
    
    @Column(name = "requested_delivery_date")
    private Timestamp requestedDeliveryDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    // Default constructor
    public InfrastructureOrderLineItemJpaEntity() {}
    
    // Constructor with required fields
    public InfrastructureOrderLineItemJpaEntity(UUID lineItemId, String materialId, BigDecimal quantity,
                                               BigDecimal unitPrice, String currency, BigDecimal lineTotal) {
        this.lineItemId = lineItemId;
        this.materialId = materialId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.currency = currency;
        this.lineTotal = lineTotal;
    }
    
    // Getters and setters
    public UUID getLineItemId() {
        return lineItemId;
    }
    
    public void setLineItemId(UUID lineItemId) {
        this.lineItemId = lineItemId;
    }
    
    public InfrastructurePurchaseOrderJpaEntity getPurchaseOrder() {
        return purchaseOrder;
    }
    
    public void setPurchaseOrder(InfrastructurePurchaseOrderJpaEntity purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }
    
    public String getMaterialId() {
        return materialId;
    }
    
    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getLineTotal() {
        return lineTotal;
    }
    
    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
    
    public Timestamp getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }
    
    public void setRequestedDeliveryDate(Timestamp requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}