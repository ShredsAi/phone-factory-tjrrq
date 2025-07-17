package ai.shreds.domain.services;

import ai.shreds.domain.exceptions.DomainPriceVarianceException;
import ai.shreds.domain.ports.DomainOutputPortSupplierCatalog;
import ai.shreds.domain.value_objects.DomainMonetaryAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DomainPriceValidationService {
    private static final BigDecimal VARIANCE_THRESHOLD = new BigDecimal("0.05"); // 5%
    private final DomainOutputPortSupplierCatalog supplierCatalog;

    public DomainPriceValidationService(DomainOutputPortSupplierCatalog supplierCatalog) {
        this.supplierCatalog = supplierCatalog;
    }

    public void validatePrice(String supplierId, String materialId, DomainMonetaryAmount requestedPrice) {
        DomainMonetaryAmount catalogPrice = supplierCatalog.getCurrentPrice(supplierId, materialId);
        BigDecimal variance = calculateVariance(catalogPrice, requestedPrice);
        
        if (!isWithinAcceptableVariance(variance)) {
            throw new DomainPriceVarianceException("Price variance of " + variance.multiply(new BigDecimal(100)) + 
                "% exceeds acceptable threshold of " + VARIANCE_THRESHOLD.multiply(new BigDecimal(100)) + "%");
        }
    }

    public BigDecimal calculateVariance(DomainMonetaryAmount catalogPrice, DomainMonetaryAmount requestedPrice) {
        if (!catalogPrice.getCurrency().equals(requestedPrice.getCurrency())) {
            throw new IllegalArgumentException("Cannot compare prices with different currencies");
        }

        BigDecimal difference = requestedPrice.getAmount().subtract(catalogPrice.getAmount()).abs();
        return difference.divide(catalogPrice.getAmount(), 4, RoundingMode.HALF_UP);
    }

    private boolean isWithinAcceptableVariance(BigDecimal variance) {
        return variance.compareTo(VARIANCE_THRESHOLD) <= 0;
    }
}