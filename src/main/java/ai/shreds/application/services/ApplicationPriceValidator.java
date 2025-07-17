package ai.shreds.application.services;

import ai.shreds.shared.dtos.SharedMonetaryAmountDTO;
import ai.shreds.domain.ports.DomainOutputPortSupplierCatalog;
import ai.shreds.domain.value_objects.DomainMonetaryAmount;
import ai.shreds.application.exceptions.ApplicationSupplierValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Validator for supplier prices against catalog prices.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationPriceValidator {

    private final DomainOutputPortSupplierCatalog domainSupplierCatalogPort;
    
    // Default acceptable price variance (5%)
    private static final BigDecimal ACCEPTABLE_VARIANCE_PERCENTAGE = new BigDecimal("0.05");
    
    /**
     * Validates that a requested price is within acceptable variance of the catalog price.
     * 
     * @param materialId The material ID to check
     * @param supplierId The supplier ID to check against
     * @param requestedPrice The requested price to validate
     * @throws ApplicationSupplierValidationException if the price variance exceeds acceptable limits
     */
    public void validatePrice(String materialId, String supplierId, BigDecimal requestedPrice) {
        log.debug("Validating price for material {} from supplier {}", materialId, supplierId);
        
        // Get current catalog price from domain
        DomainMonetaryAmount catalogPrice = domainSupplierCatalogPort.getCurrentPrice(supplierId, materialId);
        
        if (catalogPrice == null) {
            throw new ApplicationSupplierValidationException(
                "Unable to validate price for material " + materialId + " from supplier " + supplierId);
        }
        
        // Convert requested price to domain monetary amount for comparison
        DomainMonetaryAmount requestedPriceAmount = new DomainMonetaryAmount(requestedPrice, catalogPrice.getCurrency());
        
        // Calculate variance
        BigDecimal variance = calculateVariance(catalogPrice, requestedPriceAmount);
        
        if (!isWithinAcceptableVariance(variance)) {
            throw new ApplicationSupplierValidationException(
                    String.format("Price variance of %.2f%% exceeds acceptable limit of %.2f%%", 
                            variance.multiply(new BigDecimal("100")), 
                            ACCEPTABLE_VARIANCE_PERCENTAGE.multiply(new BigDecimal("100"))));
        }
        
        log.debug("Price validation succeeded for material {} with variance of {}%", 
                materialId, variance.multiply(new BigDecimal("100")));
    }
    
    /**
     * Calculates the variance between catalog price and requested price.
     * 
     * @param catalogPrice The reference catalog price
     * @param requestedPrice The requested price to compare
     * @return The calculated variance as a decimal (0.05 = 5%)
     */
    public BigDecimal calculateVariance(DomainMonetaryAmount catalogPrice, DomainMonetaryAmount requestedPrice) {
        if (!catalogPrice.getCurrency().equals(requestedPrice.getCurrency())) {
            throw new ApplicationSupplierValidationException(
                    "Currency mismatch: catalog price is in " + catalogPrice.getCurrency() + 
                    " but requested price is in " + requestedPrice.getCurrency());
        }
        
        BigDecimal catalogAmount = catalogPrice.getAmount();
        BigDecimal requestedAmount = requestedPrice.getAmount();
        
        // Protect against division by zero
        if (catalogAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new ApplicationSupplierValidationException("Cannot calculate variance: catalog price is zero");
        }
        
        // Calculate absolute difference
        BigDecimal difference = requestedAmount.subtract(catalogAmount).abs();
        
        // Calculate variance as a percentage of catalog price
        return difference.divide(catalogAmount, 4, RoundingMode.HALF_UP);
    }
    
    /**
     * Determines if a variance is within acceptable limits.
     * 
     * @param variance The variance to check
     * @return true if within limits, false otherwise
     */
    private boolean isWithinAcceptableVariance(BigDecimal variance) {
        return variance.compareTo(ACCEPTABLE_VARIANCE_PERCENTAGE) <= 0;
    }
}