package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.ports.DomainOutputPortSupplierValidation;
import ai.shreds.domain.ports.DomainOutputPortSupplierCatalog;
import ai.shreds.domain.value_objects.DomainMonetaryAmount;
import ai.shreds.shared.dtos.SharedSupplierInfoDTO;
import ai.shreds.shared.dtos.SharedSupplierValidationRequestDTO;
import ai.shreds.shared.dtos.SharedSupplierValidationResponseDTO;
import ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class InfrastructureSupplierManagementClient implements DomainOutputPortSupplierValidation, DomainOutputPortSupplierCatalog {

    private final RestTemplate restTemplate;
    private final String supplierServiceUrl;

    public InfrastructureSupplierManagementClient(RestTemplate restTemplate,
                                                  @Value("${external.supplier-management.url}") String supplierServiceUrl) {
        this.restTemplate = restTemplate;
        this.supplierServiceUrl = supplierServiceUrl;
    }

    @Override
    @CircuitBreaker(name = "supplier-validation", fallbackMethod = "validateSupplierCapabilityFallback")
    @Retry(name = "supplier-validation")
    public boolean validateSupplierCapability(String supplierId, String materialId, BigDecimal quantity) {
        try {
            SharedSupplierValidationRequestDTO request = new SharedSupplierValidationRequestDTO();
            request.setSupplierId(supplierId);
            request.setMaterialId(materialId);
            request.setQuantity(quantity);

            String url = supplierServiceUrl + "/api/internal/v1/suppliers/{supplierId}/validate-item?materialId={materialId}&quantity={quantity}";
            
            SharedSupplierValidationResponseDTO response = restTemplate.getForObject(
                    url,
                    SharedSupplierValidationResponseDTO.class,
                    supplierId,
                    materialId,
                    quantity
            );
            
            return response != null && Boolean.TRUE.equals(response.getIsAvailable());
        } catch (RestClientException e) {
            throw new InfrastructureExternalServiceException("Supplier Management Service", 
                    "Failed to validate supplier capability: " + e.getMessage(), e);
        }
    }

    @Override
    @CircuitBreaker(name = "supplier-info", fallbackMethod = "getSupplierInfoFallback")
    @Retry(name = "supplier-info")
    public SharedSupplierInfoDTO getSupplierInfo(String supplierId) {
        try {
            String url = supplierServiceUrl + "/api/internal/v1/suppliers/{supplierId}";
            
            SharedSupplierInfoDTO response = restTemplate.getForObject(
                    url,
                    SharedSupplierInfoDTO.class,
                    supplierId
            );
            
            return response;
        } catch (RestClientException e) {
            throw new InfrastructureExternalServiceException("Supplier Management Service", 
                    "Failed to get supplier info: " + e.getMessage(), e);
        }
    }

    @Override
    @CircuitBreaker(name = "supplier-catalog", fallbackMethod = "getCurrentPriceFallback")
    @Retry(name = "supplier-catalog")
    public DomainMonetaryAmount getCurrentPrice(String supplierId, String materialId) {
        try {
            String url = supplierServiceUrl + "/api/internal/v1/suppliers/{supplierId}/catalog/{materialId}/price";
            
            Map<String, Object> response = restTemplate.getForObject(
                    url,
                    Map.class,
                    supplierId,
                    materialId
            );
            
            if (response != null && response.containsKey("price") && response.containsKey("currency")) {
                BigDecimal price = new BigDecimal(response.get("price").toString());
                String currency = response.get("currency").toString();
                return new DomainMonetaryAmount(price, currency);
            }
            
            throw new InfrastructureExternalServiceException("Supplier Management Service", 
                    "Invalid price response format");
        } catch (RestClientException e) {
            throw new InfrastructureExternalServiceException("Supplier Management Service", 
                    "Failed to get current price: " + e.getMessage(), e);
        }
    }

    @Override
    @CircuitBreaker(name = "supplier-catalog", fallbackMethod = "getLeadTimeFallback")
    @Retry(name = "supplier-catalog")
    public Integer getLeadTime(String supplierId, String materialId) {
        try {
            String url = supplierServiceUrl + "/api/internal/v1/suppliers/{supplierId}/catalog/{materialId}/lead-time";
            
            Map<String, Object> response = restTemplate.getForObject(
                    url,
                    Map.class,
                    supplierId,
                    materialId
            );
            
            if (response != null && response.containsKey("leadTimeDays")) {
                return Integer.valueOf(response.get("leadTimeDays").toString());
            }
            
            throw new InfrastructureExternalServiceException("Supplier Management Service", 
                    "Invalid lead time response format");
        } catch (RestClientException e) {
            throw new InfrastructureExternalServiceException("Supplier Management Service", 
                    "Failed to get lead time: " + e.getMessage(), e);
        }
    }

    // Fallback methods for circuit breaker
    public boolean validateSupplierCapabilityFallback(String supplierId, String materialId, BigDecimal quantity, Exception ex) {
        // Return false as safe default when service is unavailable
        return false;
    }

    public SharedSupplierInfoDTO getSupplierInfoFallback(String supplierId, Exception ex) {
        // Return basic info indicating service unavailable
        SharedSupplierInfoDTO fallback = new SharedSupplierInfoDTO();
        fallback.setSupplierId(supplierId);
        fallback.setCompanyName("Service Unavailable");
        fallback.setStatus("UNKNOWN");
        fallback.setIsActive(false);
        fallback.setLeadTime(0);
        return fallback;
    }

    public DomainMonetaryAmount getCurrentPriceFallback(String supplierId, String materialId, Exception ex) {
        // Return zero price as fallback
        return new DomainMonetaryAmount(BigDecimal.ZERO, "USD");
    }

    public Integer getLeadTimeFallback(String supplierId, String materialId, Exception ex) {
        // Return default lead time of 30 days
        return 30;
    }
}