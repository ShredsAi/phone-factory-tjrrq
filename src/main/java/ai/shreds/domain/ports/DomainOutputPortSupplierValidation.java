package ai.shreds.domain.ports;

import ai.shreds.shared.dtos.SharedSupplierInfoDTO;

/**
 * Domain output port for supplier validation operations.
 * This port is implemented by infrastructure clients that connect to the Supplier Management service.
 */
public interface DomainOutputPortSupplierValidation {
    /**
     * Validates if a supplier can fulfill an order for a specific material and quantity.
     * 
     * @param supplierId The ID of the supplier to validate
     * @param materialId The ID of the material being ordered
     * @param quantity The quantity being ordered
     * @return true if the supplier can fulfill the order, false otherwise
     * @throws ai.shreds.domain.exceptions.DomainSupplierNotActiveException if the supplier is not active
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if validation fails due to service issues
     */
    boolean validateSupplierCapability(String supplierId, String materialId, java.math.BigDecimal quantity);
    
    /**
     * Retrieves detailed information about a supplier.
     * 
     * @param supplierId The ID of the supplier
     * @return DTO containing supplier information
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if retrieval fails due to service issues
     */
    SharedSupplierInfoDTO getSupplierInfo(String supplierId);
}