package ai.shreds.domain.value_objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing a supplier's performance metrics used for evaluation.
 */
public class DomainPerformanceMetrics {
    private final BigDecimal onTimeDeliveryRate;
    private final Integer qualityScore;
    private final Integer totalOrdersCompleted;
    private final Integer averageLeadTime;
    
    private static final BigDecimal MIN_ON_TIME_RATE = new BigDecimal("0.80"); // 80%
    private static final Integer MIN_QUALITY_SCORE = 6; // On scale of 1-10

    /**
     * Creates a new performance metrics value object.
     *
     * @param onTimeDeliveryRate the percentage (0-100%) of orders delivered on time
     * @param qualityScore the quality rating (1-10) of the supplier's products
     * @param totalOrdersCompleted the total number of orders completed with this supplier
     * @param averageLeadTime the average lead time in days for this supplier's orders
     */
    public DomainPerformanceMetrics(BigDecimal onTimeDeliveryRate, Integer qualityScore, 
                                  Integer totalOrdersCompleted, Integer averageLeadTime) {
        if (onTimeDeliveryRate == null || onTimeDeliveryRate.compareTo(BigDecimal.ZERO) < 0 || 
                onTimeDeliveryRate.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("On-time delivery rate must be between 0% and 100%");
        }
        if (qualityScore == null || qualityScore < 1 || qualityScore > 10) {
            throw new IllegalArgumentException("Quality score must be between 1 and 10");
        }
        if (totalOrdersCompleted == null || totalOrdersCompleted < 0) {
            throw new IllegalArgumentException("Total orders completed must be 0 or greater");
        }
        if (averageLeadTime == null || averageLeadTime <= 0) {
            throw new IllegalArgumentException("Average lead time must be greater than 0 days");
        }
        
        this.onTimeDeliveryRate = onTimeDeliveryRate;
        this.qualityScore = qualityScore;
        this.totalOrdersCompleted = totalOrdersCompleted;
        this.averageLeadTime = averageLeadTime;
    }

    /**
     * Calculates an overall performance score based on weighted metrics.
     *
     * @return a score between 0 and 1 representing overall supplier performance
     */
    public BigDecimal calculateOverallScore() {
        // Convert on-time delivery rate to a scale of 0-1
        BigDecimal deliveryScore = this.onTimeDeliveryRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // Convert quality score to a scale of 0-1
        BigDecimal normalizedQualityScore = new BigDecimal(this.qualityScore).divide(new BigDecimal("10"), 2, RoundingMode.HALF_UP);
        
        // Calculate weighted average (60% delivery, 40% quality)
        BigDecimal weightedDelivery = deliveryScore.multiply(new BigDecimal("0.60"));
        BigDecimal weightedQuality = normalizedQualityScore.multiply(new BigDecimal("0.40"));
        
        return weightedDelivery.add(weightedQuality);
    }

    /**
     * Determines if the supplier meets minimum performance standards.
     *
     * @return true if the supplier meets minimum standards, false otherwise
     */
    public boolean meetsMinimumStandards() {
        boolean deliveryMeetsStandard = this.onTimeDeliveryRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                .compareTo(MIN_ON_TIME_RATE) >= 0;
                
        boolean qualityMeetsStandard = this.qualityScore >= MIN_QUALITY_SCORE;
        
        // Supplier must meet both delivery and quality standards
        return deliveryMeetsStandard && qualityMeetsStandard;
    }
    
    // Getters
    public BigDecimal getOnTimeDeliveryRate() {
        return onTimeDeliveryRate;
    }

    public Integer getQualityScore() {
        return qualityScore;
    }

    public Integer getTotalOrdersCompleted() {
        return totalOrdersCompleted;
    }

    public Integer getAverageLeadTime() {
        return averageLeadTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainPerformanceMetrics)) return false;
        DomainPerformanceMetrics that = (DomainPerformanceMetrics) o;
        return Objects.equals(onTimeDeliveryRate, that.onTimeDeliveryRate) &&
               Objects.equals(qualityScore, that.qualityScore) &&
               Objects.equals(totalOrdersCompleted, that.totalOrdersCompleted) &&
               Objects.equals(averageLeadTime, that.averageLeadTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(onTimeDeliveryRate, qualityScore, totalOrdersCompleted, averageLeadTime);
    }

    @Override
    public String toString() {
        return "DomainPerformanceMetrics{" +
                "onTimeDeliveryRate=" + onTimeDeliveryRate + "%" +
                ", qualityScore=" + qualityScore + "/10" +
                ", totalOrdersCompleted=" + totalOrdersCompleted +
                ", averageLeadTime=" + averageLeadTime + " days" +
                '}';
    }
}