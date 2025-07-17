package ai.shreds.domain.ports;

import ai.shreds.domain.value_objects.DomainMonetaryAmount;

/**
 * Domain output port for supplier catalog operations.
 * This port is implemented by infrastructure clients that connect to the Supplier Management service.
 */
public interface DomainOutputPortSupplierCatalog {
    /**
     * Retrieves the current price for a specific material from a supplier's catalog.
     * 
     * @param supplierId The ID of the supplier
     * @param materialId The ID of the material
     * @return The current price as a monetary amount
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if retrieval fails due to service issues
     */
    DomainMonetaryAmount getCurrentPrice(String supplierId, String materialId);
    
    /**
     * Retrieves the lead time in days for a specific material from a supplier.
     * 
     * @param supplierId The ID of the supplier
     * @param materialId The ID of the material
     * @return The lead time in days
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if retrieval fails due to service issues
     */
    Integer getLeadTime(String supplierId, String materialId);
}