package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.ports.DomainOutputPortEdiTransmission;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class InfrastructureEdiClient implements DomainOutputPortEdiTransmission {

    private final RestTemplate restTemplate;
    private final String ediGatewayUrl;

    public InfrastructureEdiClient(RestTemplate restTemplate,
                                   @Value("${external.edi-gateway.url}") String ediGatewayUrl) {
        this.restTemplate = restTemplate;
        this.ediGatewayUrl = ediGatewayUrl;
    }

    @Override
    @CircuitBreaker(name = "edi-gateway", fallbackMethod = "sendEdiOrderFallback")
    @Retry(name = "edi-gateway")
    public String sendEdiOrder(String supplierId, SharedPurchaseOrderDataDTO orderData) {
        try {
            String ediMessage = convertToEdiFormat(orderData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-EDI-Partner-Id", supplierId);
            headers.set("X-EDI-Message-Type", "850"); // EDI 850 is Purchase Order
            
            Map<String, Object> request = new HashMap<>();
            request.put("partnerId", supplierId);
            request.put("messageType", "850");
            request.put("ediMessage", ediMessage);
            request.put("orderId", orderData.getOrderId());
            
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    ediGatewayUrl + "/api/v1/edi/send",
                    HttpMethod.POST,
                    httpEntity,
                    Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                return responseBody.get("transactionId") != null ? 
                       responseBody.get("transactionId").toString() : 
                       "UNKNOWN_TRANSACTION_ID";
            }
            
            throw new InfrastructureExternalServiceException(
                    "EDI Gateway", 
                    "Failed to send EDI order to supplier: " + supplierId
            );
            
        } catch (RestClientException e) {
            throw new InfrastructureExternalServiceException(
                    "EDI Gateway", 
                    "Failed to send EDI order to supplier: " + supplierId, 
                    e
            );
        }
    }

    @Override
    @CircuitBreaker(name = "edi-gateway", fallbackMethod = "receiveEdiAcknowledgmentFallback")
    @Retry(name = "edi-gateway")
    public String receiveEdiAcknowledgment(String transactionId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    ediGatewayUrl + "/api/v1/edi/acknowledgment/{transactionId}",
                    HttpMethod.GET,
                    request,
                    Map.class,
                    transactionId
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
                    "EDI Gateway", 
                    "Failed to receive EDI acknowledgment for transaction: " + transactionId, 
                    e
            );
        }
    }

    private String convertToEdiFormat(SharedPurchaseOrderDataDTO orderData) {
        // This is a simplified EDI 850 format conversion
        // In a real implementation, this would use a proper EDI library
        StringBuilder ediMessage = new StringBuilder();
        
        // ISA segment (Interchange Control Header)
        ediMessage.append("ISA*00*          *00*          *ZZ*BUYER         *ZZ*SUPPLIER      *")
                  .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd")))
                  .append("*")
                  .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm")))
                  .append("*U*00405*000000001*0*P*>~");
        
        // GS segment (Functional Group Header)
        ediMessage.append("GS*PO*BUYER*SUPPLIER*")
                  .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                  .append("*")
                  .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm")))
                  .append("*1*X*004050~");
        
        // ST segment (Transaction Set Header)
        ediMessage.append("ST*850*0001~");
        
        // BEG segment (Beginning Segment for Purchase Order)
        ediMessage.append("BEG*00*SA*").append(orderData.getOrderId())
                  .append("**").append(orderData.getOrderDate()).append("~");
        
        // Add line items
        if (orderData.getLineItems() != null) {
            int lineNumber = 1;
            for (var lineItem : orderData.getLineItems()) {
                // PO1 segment (Baseline Item Data)
                ediMessage.append("PO1*").append(lineNumber++)
                          .append("*").append(lineItem.getQuantity())
                          .append("*EA*").append(lineItem.getUnitPrice().getAmount())
                          .append("**IN*").append(lineItem.getMaterialId())
                          .append("~");
            }
        }
        
        // CTT segment (Transaction Totals)
        int totalLines = orderData.getLineItems() != null ? orderData.getLineItems().size() : 0;
        ediMessage.append("CTT*").append(totalLines).append("~");
        
        // SE segment (Transaction Set Trailer)
        ediMessage.append("SE*").append(totalLines + 4).append("*0001~");
        
        // GE segment (Functional Group Trailer)
        ediMessage.append("GE*1*1~");
        
        // IEA segment (Interchange Control Trailer)
        ediMessage.append("IEA*1*000000001~");
        
        return ediMessage.toString();
    }

    // Fallback methods
    public String sendEdiOrderFallback(String supplierId, SharedPurchaseOrderDataDTO orderData, Exception ex) {
        return "FALLBACK_EDI_TRANSACTION_" + System.currentTimeMillis();
    }

    public String receiveEdiAcknowledgmentFallback(String transactionId, Exception ex) {
        return "UNKNOWN";
    }
}