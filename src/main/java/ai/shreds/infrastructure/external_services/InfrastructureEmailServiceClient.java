package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.ports.DomainOutputPortEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class InfrastructureEmailServiceClient implements DomainOutputPortEmailService {

    private final RestTemplate restTemplate;
    private final String emailServiceUrl;

    public InfrastructureEmailServiceClient(RestTemplate restTemplate,
                                            @Value("${external.email-service.url}") String emailServiceUrl) {
        this.restTemplate = restTemplate;
        this.emailServiceUrl = emailServiceUrl;
    }

    @Override
    public void sendPurchaseOrderEmail(String recipientEmail, byte[] orderPdf, String orderId) {
        Map<String, Object> request = new HashMap<>();
        request.put("to", recipientEmail);
        request.put("subject", "Purchase Order: " + orderId);
        request.put("body", "Please find attached Purchase Order " + orderId);
        request.put("attachmentName", orderId + ".pdf");
        request.put("attachmentData", Base64.getEncoder().encodeToString(orderPdf));
        restTemplate.postForEntity(emailServiceUrl + "/api/v1/emails/send-with-attachment", request, Void.class);
    }

    @Override
    public void sendNotificationEmail(String recipientEmail, String subject, String body) {
        Map<String, Object> request = new HashMap<>();
        request.put("to", recipientEmail);
        request.put("subject", subject);
        request.put("body", body);
        restTemplate.postForEntity(emailServiceUrl + "/api/v1/emails/send", request, Void.class);
    }
}
