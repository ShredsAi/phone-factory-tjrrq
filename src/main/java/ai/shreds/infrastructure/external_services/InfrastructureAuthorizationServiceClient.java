package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.ports.DomainOutputPortAuthorization;
import ai.shreds.domain.value_objects.DomainMonetaryAmount;
import ai.shreds.shared.dtos.SharedAuthorizationRequestDTO;
import ai.shreds.shared.dtos.SharedAuthorizationResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Infrastructure implementation of authorization port using external auth service.
 */
@Component
public class InfrastructureAuthorizationServiceClient implements DomainOutputPortAuthorization {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public InfrastructureAuthorizationServiceClient(RestTemplate restTemplate,
                                                     @Value("${external.auth-service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    /**
     * Validates that the user has authority to approve a purchase order of the given value.
     */
    @Override
    public boolean validateApprovalAuthority(String userId, DomainMonetaryAmount orderValue) {
        BigDecimal amount = orderValue.getAmount();
        String currency = orderValue.getCurrency();
        SharedAuthorizationRequestDTO request = new SharedAuthorizationRequestDTO(
                userId,
                "PurchaseOrder",
                "APPROVE",
                Map.of(
                        "orderValue", amount,
                        "currency", currency
                )
        );
        SharedAuthorizationResponseDTO response = restTemplate.postForObject(
                authServiceUrl + "/api/v1/auth/validate-approval",
                request,
                SharedAuthorizationResponseDTO.class
        );
        return response != null && Boolean.TRUE.equals(response.getIsAuthorized());
    }

    /**
     * Retrieves the approval level for the given user.
     */
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