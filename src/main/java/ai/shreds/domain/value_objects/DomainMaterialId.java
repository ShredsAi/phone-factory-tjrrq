package ai.shreds.domain.value_objects;

public class DomainMaterialId {
    private final String value;

    public DomainMaterialId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Material ID cannot be null or empty");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainMaterialId)) return false;
        DomainMaterialId that = (DomainMaterialId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "DomainMaterialId{" +
                "value='" + value + '\'' +
                '}';
    }
}