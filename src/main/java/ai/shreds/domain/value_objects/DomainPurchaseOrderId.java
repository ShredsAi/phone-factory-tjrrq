package ai.shreds.domain.value_objects;

public class DomainPurchaseOrderId {
    private final String value;

    public DomainPurchaseOrderId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Purchase Order ID cannot be null or empty");
        }
        if (!value.matches("^PO-\\d{4}-\\d{3}$")) {
            throw new IllegalArgumentException("Purchase Order ID must follow format PO-YYYY-XXX");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainPurchaseOrderId)) return false;
        DomainPurchaseOrderId that = (DomainPurchaseOrderId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "DomainPurchaseOrderId{" +
                "value='" + value + '\'' +
                '}';
    }
}