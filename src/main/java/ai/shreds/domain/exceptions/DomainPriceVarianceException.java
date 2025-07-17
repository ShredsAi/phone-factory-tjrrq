package ai.shreds.domain.exceptions;

import java.math.BigDecimal;

/**
 * Exception thrown when a requested unit price varies too much from the current
 * catalog price for a material.
 */
public class DomainPriceVarianceException extends RuntimeException {

    private final BigDecimal variance;

    /**
     * Creates a new DomainPriceVarianceException with the calculated variance.
     *
     * @param variance the percentage variance between catalog and requested price
     */
    public DomainPriceVarianceException(BigDecimal variance) {
        super("Price variance of " + variance + "% exceeds allowed threshold");
        this.variance = variance;
    }

    /**
     * Creates a new DomainPriceVarianceException with a custom message.
     *
     * @param message the exception message
     */
    public DomainPriceVarianceException(String message) {
        super(message);
        this.variance = BigDecimal.ZERO; // Default for custom messages
    }

    /**
     * Gets the calculated variance that triggered this exception.
     *
     * @return the percentage variance between catalog and requested price
     */
    public BigDecimal getVariance() {
        return variance;
    }
}