package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainContactInfo;
import ai.shreds.domain.value_objects.DomainMaterialId;
import ai.shreds.domain.value_objects.DomainPerformanceMetrics;
import ai.shreds.domain.value_objects.DomainSupplierId;
import ai.shreds.shared.dtos.SharedSupplierInfoDTO;

import java.math.BigDecimal;

/**
 * Domain entity representing a supplier in the system.
 * Contains supplier identification, contact information, and performance data.
 */
public class DomainSupplierEntity {
    private final DomainSupplierId supplierId;
    private String companyName;
    private DomainContactInfo contactInfo;
    private DomainPerformanceMetrics performanceMetrics;
    private String status;
    private String paymentTerms;

    /**
     * Creates a new supplier entity with the required information.
     *
     * @param supplierId the unique identifier of the supplier
     * @param companyName the name of the supplier company
     * @param contactInfo the contact information of the supplier
     * @param performanceMetrics metrics tracking the supplier's performance
     * @param status the current status of the supplier relationship
     * @param paymentTerms the standard payment terms for this supplier
     */
    public DomainSupplierEntity(DomainSupplierId supplierId,
                               String companyName,
                               DomainContactInfo contactInfo,
                               DomainPerformanceMetrics performanceMetrics,
                               String status,
                               String paymentTerms) {
        if (supplierId == null) {
            throw new IllegalArgumentException("supplierId cannot be null");
        }
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("companyName cannot be null or empty");
        }
        if (contactInfo == null) {
            throw new IllegalArgumentException("contactInfo cannot be null");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("status cannot be null or empty");
        }
        
        this.supplierId = supplierId;
        this.companyName = companyName;
        this.contactInfo = contactInfo;
        this.performanceMetrics = performanceMetrics;
        this.status = status;
        this.paymentTerms = paymentTerms;
    }

    /**
     * Evaluates the supplier's performance based on current metrics.
     * This may update the status of the supplier relationship.
     */
    public void evaluatePerformance() {
        if (performanceMetrics != null) {
            boolean meetsStandards = performanceMetrics.meetsMinimumStandards();
            if (!meetsStandards && isActive()) {
                suspendSupplier("Failed to meet minimum performance standards");
            }
        }
    }

    /**
     * Suspends a supplier with a specific reason.
     * 
     * @param reason the reason for suspension
     */
    public void suspendSupplier(String reason) {
        this.status = "SUSPENDED";
        // In a real implementation, this might log the reason or notify relevant personnel
    }

    /**
     * Reactivates a previously suspended supplier.
     */
    public void reactivateSupplier() {
        this.status = "ACTIVE";
    }

    /**
     * Checks if a supplier can fulfill an order for a specific material and quantity.
     * 
     * @param materialId the ID of the material
     * @param quantity the quantity being ordered
     * @return true if the supplier can fulfill the order, false otherwise
     */
    public boolean canFulfillOrder(DomainMaterialId materialId, BigDecimal quantity) {
        // This is a simplified implementation
        // In a real system, this would check capacity, material availability, etc.
        return isActive() && quantity.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if the supplier is currently active.
     * 
     * @return true if the supplier is active, false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    /**
     * Converts this domain entity to a shared DTO representation.
     * 
     * @return a DTO containing supplier information
     */
    public SharedSupplierInfoDTO toSharedDTO() {
        SharedSupplierInfoDTO dto = new SharedSupplierInfoDTO();
        dto.setSupplierId(this.supplierId.getValue());
        dto.setCompanyName(this.companyName);
        dto.setStatus(this.status);
        
        if (this.performanceMetrics != null) {
            dto.setLeadTime(this.performanceMetrics.getAverageLeadTime());
        }
        
        dto.setIsActive(this.isActive());
        return dto;
    }

    // Getters
    public DomainSupplierId getSupplierId() {
        return supplierId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public DomainContactInfo getContactInfo() {
        return contactInfo;
    }

    public DomainPerformanceMetrics getPerformanceMetrics() {
        return performanceMetrics;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }
    
    // Setters for mutable properties
    public void setCompanyName(String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("companyName cannot be null or empty");
        }
        this.companyName = companyName;
    }

    public void setContactInfo(DomainContactInfo contactInfo) {
        if (contactInfo == null) {
            throw new IllegalArgumentException("contactInfo cannot be null");
        }
        this.contactInfo = contactInfo;
    }

    public void setPerformanceMetrics(DomainPerformanceMetrics performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }
    
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }
}