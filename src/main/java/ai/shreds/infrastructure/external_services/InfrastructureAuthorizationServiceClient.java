package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.ports.DomainOutputPortAuthorization;
import ai.shreds.shared.dtos.SharedAuthorizationRequestDTO;
import ai.shreds.shared.dtos.SharedAuthorizationResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class InfrastructureAuthorizationServiceClient implements DomainOutputPortAuthorization {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public InfrastructureAuthorizationServiceClient(RestTemplate restTemplate,
                                                     @Value("${external.auth-service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public boolean validateApprovalAuthority(String userId, BigDecimal orderValue) {
        SharedAuthorizationRequestDTO request = new SharedAuthorizationRequestDTO(
                userId,
                "PurchaseOrder",
                "APPROVE",
                Map.of(
                        "orderValue", orderValue,
                        "currency", "USD"
                )
        );
        SharedAuthorizationResponseDTO response = restTemplate.postForObject(
                authServiceUrl + "/api/v1/auth/validate-approval",
                request,
                SharedAuthorizationResponseDTO.class
        );
        return response != null && Boolean.TRUE.equals(response.getIsAuthorized());
    }

    @Override
    public String getUserApprovalLevel(String userId) {
        SharedAuthorizationResponseDTO response = restTemplate.getForObject(
                authServiceUrl + "/api/v1/auth/user-level/{userId}",
                SharedAuthorizationResponseDTO.class,
                userId
        );
        return response != null ? response.getUserLevel() : null;
    }
}
