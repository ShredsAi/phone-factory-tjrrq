package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.ports.DomainOutputPortSupplierApi;
import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;
import ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.HashMap;

@Component
public class InfrastructureSupplierApiClient implements DomainOutputPortSupplierApi {

    private final RestTemplate restTemplate;
    private final String defaultSupplierApiUrl;

    public InfrastructureSupplierApiClient(RestTemplate restTemplate,
                                           @Value("${external.supplier-api.default-url:https://api.supplier.com}") String defaultSupplierApiUrl) {
        this.restTemplate = restTemplate;
        this.defaultSupplierApiUrl = defaultSupplierApiUrl;
    }

    @Override
    @CircuitBreaker(name = "supplier-api", fallbackMethod = "transmitPurchaseOrderFallback")
    @Retry(name = "supplier-api")
    public String transmitPurchaseOrder(String supplierId, SharedPurchaseOrderDataDTO orderData) {
        try {
            String supplierEndpoint = getSupplierEndpoint(supplierId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-Key", getSupplierApiKey(supplierId));
            
            // Convert to supplier-specific format
            Map<String, Object> supplierOrderData = convertToSupplierFormat(orderData);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(supplierOrderData, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    supplierEndpoint + "/orders",
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return responseBody.get("supplierOrderId") != null ? 
                       responseBody.get("supplierOrderId").toString() : 
                       "UNKNOWN_ORDER_ID";
            }
            
            throw new InfrastructureExternalServiceException(
                    "Supplier API", 
                    "Failed to transmit purchase order to supplier: " + supplierId
            );
            
        } catch (RestClientException e) {
            throw new InfrastructureExternalServiceException(
                    "Supplier API", 
                    "Failed to transmit purchase order to supplier: " + supplierId, 
                    e
            );
        }
    }

    @Override
    @CircuitBreaker(name = "supplier-api", fallbackMethod = "checkOrderStatusFallback")
    @Retry(name = "supplier-api")
    public String checkOrderStatus(String supplierId, String supplierOrderId) {
        try {
            String supplierEndpoint = getSupplierEndpoint(supplierId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-Key", getSupplierApiKey(supplierId));
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    supplierEndpoint + "/orders/{supplierOrderId}/status",
                    HttpMethod.GET,
                    request,
                    Map.class,
                    supplierOrderId
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return responseBody.get("status") != null ? 
                       responseBody.get("status").toString() : 
                       "UNKNOWN";
            }
            
            return "UNKNOWN";
            
        } catch (RestClientException e) {
            throw new InfrastructureExternalServiceException(
                    "Supplier API", 
                    "Failed to check order status for supplier: " + supplierId + ", order: " + supplierOrderId, 
                    e
            );
        }
    }

    private String getSupplierEndpoint(String supplierId) {
        // In a real implementation, this would lookup supplier-specific endpoints
        // from a configuration service or database
        return defaultSupplierApiUrl;
    }

    private String getSupplierApiKey(String supplierId) {
        // In a real implementation, this would retrieve supplier-specific API keys
        // from a secure configuration service or vault
        return "default-api-key";
    }

    private Map<String, Object> convertToSupplierFormat(SharedPurchaseOrderDataDTO orderData) {
        Map<String, Object> supplierFormat = new HashMap<>();
        supplierFormat.put("poReference", orderData.getOrderId());
        supplierFormat.put("issueDate", orderData.getOrderDate());
        supplierFormat.put("expectedDeliveryDate", orderData.getExpectedDeliveryDate());
        supplierFormat.put("paymentTerms", orderData.getPaymentTerms());
        supplierFormat.put("deliveryConditions", orderData.getDeliveryConditions());
        
        // Convert line items
        if (orderData.getLineItems() != null) {
            supplierFormat.put("lineItems", orderData.getLineItems().stream()
                    .map(lineItem -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("sku", lineItem.getMaterialId());
                        item.put("quantity", lineItem.getQuantity());
                        item.put("unitPrice", lineItem.getUnitPrice().getAmount());
                        item.put("currency", lineItem.getUnitPrice().getCurrency());
                        item.put("requestedDeliveryDate", lineItem.getRequestedDeliveryDate());
                        return item;
                    })
                    .toList());
        }
        
        return supplierFormat;
    }

    // Fallback methods
    public String transmitPurchaseOrderFallback(String supplierId, SharedPurchaseOrderDataDTO orderData, Exception ex) {
        return "FALLBACK_ORDER_ID_" + System.currentTimeMillis();
    }

    public String checkOrderStatusFallback(String supplierId, String supplierOrderId, Exception ex) {
        return "UNKNOWN";
    }
}