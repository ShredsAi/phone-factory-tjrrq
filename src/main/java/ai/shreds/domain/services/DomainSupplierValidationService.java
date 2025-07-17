package ai.shreds.domain.services;

import ai.shreds.domain.exceptions.DomainSupplierNotActiveException;
import ai.shreds.domain.ports.DomainOutputPortSupplierValidation;
import ai.shreds.domain.ports.DomainOutputPortSupplierCatalog;
import ai.shreds.shared.dtos.SharedSupplierInfoDTO;

import java.math.BigDecimal;

public class DomainSupplierValidationService {
    private final DomainOutputPortSupplierValidation supplierValidation;
    private final DomainOutputPortSupplierCatalog supplierCatalog;

    public DomainSupplierValidationService(DomainOutputPortSupplierValidation supplierValidation,
                                          DomainOutputPortSupplierCatalog supplierCatalog) {
        this.supplierValidation = supplierValidation;
        this.supplierCatalog = supplierCatalog;
    }

    public boolean validateSupplier(String supplierId) {
        SharedSupplierInfoDTO supplierInfo = supplierValidation.getSupplierInfo(supplierId);
        if (!supplierInfo.getIsActive()) {
            throw new DomainSupplierNotActiveException(supplierId);
        }
        return true;
    }

    public boolean validateSupplierCapability(String supplierId, String materialId, BigDecimal quantity) {
        if (!validateSupplier(supplierId)) {
            return false;
        }
        return supplierValidation.validateSupplierCapability(supplierId, materialId, quantity);
    }

    public Integer getSupplierLeadTime(String supplierId, String materialId) {
        validateSupplier(supplierId);
        return supplierCatalog.getLeadTime(supplierId, materialId);
    }
}