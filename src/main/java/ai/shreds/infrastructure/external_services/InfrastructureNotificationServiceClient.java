package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.ports.DomainOutputPortNotification;
import ai.shreds.domain.value_objects.DomainMonetaryAmount;
import ai.shreds.shared.dtos.SharedNotificationRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class InfrastructureNotificationServiceClient implements DomainOutputPortNotification {

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;

    public InfrastructureNotificationServiceClient(RestTemplate restTemplate,
                                                    @Value("${external.notification.url}") String notificationServiceUrl) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    @Override
    public void sendApprovalNotification(String recipientId, String orderId, DomainMonetaryAmount orderValue) {
        SharedNotificationRequestDTO request = new SharedNotificationRequestDTO(
                recipientId,
                "EMAIL",
                "PO_APPROVAL_REQUEST",
                Map.of(
                        "orderId", orderId,
                        "totalAmount", orderValue.getAmount().toPlainString() + " " + orderValue.getCurrency()
                )
        );
        restTemplate.postForEntity(notificationServiceUrl + "/api/v1/notifications", request, Void.class);
    }

    @Override
    public void sendOrderStatusNotification(String recipientId, String orderId, String newStatus) {
        SharedNotificationRequestDTO request = new SharedNotificationRequestDTO(
                recipientId,
                "EMAIL",
                "PO_STATUS_UPDATE",
                Map.of(
                        "orderId", orderId,
                        "newStatus", newStatus
                )
        );
        restTemplate.postForEntity(notificationServiceUrl + "/api/v1/notifications", request, Void.class);
    }
}
