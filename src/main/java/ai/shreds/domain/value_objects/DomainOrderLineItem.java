package ai.shreds.domain.value_objects;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class DomainOrderLineItem {
    private final UUID lineItemId;
    private final DomainMaterialId materialId;
    private final BigDecimal quantity;
    private final DomainMonetaryAmount unitPrice;
    private final LocalDateTime requestedDeliveryDate;
    private final String notes;

    public DomainOrderLineItem(UUID lineItemId, DomainMaterialId materialId, BigDecimal quantity,
                               DomainMonetaryAmount unitPrice, LocalDateTime requestedDeliveryDate, String notes) {
        if (lineItemId == null) {
            throw new IllegalArgumentException("LineItemId cannot be null");
        }
        if (materialId == null) {
            throw new IllegalArgumentException("MaterialId cannot be null");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("UnitPrice cannot be null");
        }
        this.lineItemId = lineItemId;
        this.materialId = materialId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.requestedDeliveryDate = requestedDeliveryDate;
        this.notes = notes;
    }

    public UUID getLineItemId() {
        return lineItemId;
    }

    public DomainMaterialId getMaterialId() {
        return materialId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public DomainMonetaryAmount getUnitPrice() {
        return unitPrice;
    }

    public LocalDateTime getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public String getNotes() {
        return notes;
    }

    public DomainMonetaryAmount calculateLineTotal() {
        BigDecimal total = unitPrice.getAmount().multiply(quantity);
        return new DomainMonetaryAmount(total, unitPrice.getCurrency());
    }

    public boolean validateQuantity() {
        return quantity.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainOrderLineItem)) return false;
        DomainOrderLineItem that = (DomainOrderLineItem) o;
        return lineItemId.equals(that.lineItemId);
    }

    @Override
    public int hashCode() {
        return lineItemId.hashCode();
    }

    @Override
    public String toString() {
        return "DomainOrderLineItem{" +
                "lineItemId=" + lineItemId +
                ", materialId=" + materialId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", requestedDeliveryDate=" + requestedDeliveryDate +
                '}' ;
    }
}