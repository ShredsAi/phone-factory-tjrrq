package ai.shreds.domain.value_objects;

import java.math.BigDecimal;
import ai.shreds.shared.dtos.SharedMonetaryAmountDTO;

public class DomainMonetaryAmount {
    private final BigDecimal amount;
    private final String currency;

    public DomainMonetaryAmount(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("Amount must be non-negative with max 2 decimal places");
        }
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a valid ISO 4217 code");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public DomainMonetaryAmount add(DomainMonetaryAmount other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add amounts with different currencies");
        }
        return new DomainMonetaryAmount(this.amount.add(other.amount), this.currency);
    }

    public SharedMonetaryAmountDTO toSharedDTO() {
        SharedMonetaryAmountDTO dto = new SharedMonetaryAmountDTO();
        dto.setAmount(this.amount);
        dto.setCurrency(this.currency);
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainMonetaryAmount)) return false;
        DomainMonetaryAmount that = (DomainMonetaryAmount) o;
        return amount.equals(that.amount) && currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        int result = amount.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}