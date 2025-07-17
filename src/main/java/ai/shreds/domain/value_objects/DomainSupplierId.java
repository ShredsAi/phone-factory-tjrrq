package ai.shreds.domain.value_objects;

public class DomainSupplierId {
    private final String value;

    public DomainSupplierId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier ID cannot be null or empty");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainSupplierId)) return false;
        DomainSupplierId that = (DomainSupplierId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "DomainSupplierId{" +
                "value='" + value + '\'' +
                '}';
    }
}