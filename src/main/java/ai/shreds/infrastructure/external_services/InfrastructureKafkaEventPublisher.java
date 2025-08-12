package ai.shreds.infrastructure.external_services;

import ai.shreds.application.ports.ApplicationKafkaOutputPort;
import ai.shreds.shared.dtos.SharedPurchaseOrderTransmittedEventDTO;
import ai.shreds.infrastructure.exceptions.InfrastructureKafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class InfrastructureKafkaEventPublisher implements ApplicationKafkaOutputPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String purchaseOrderTopic;

    public InfrastructureKafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                             @Value("${spring.kafka.producer.topic}") String purchaseOrderTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.purchaseOrderTopic = purchaseOrderTopic;
    }

    @Override
    public void publishPurchaseOrderTransmitted(SharedPurchaseOrderTransmittedEventDTO event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    purchaseOrderTopic,
                    event.getOrderId(), // Use orderId as message key for partitioning
                    event
            );

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    throw new InfrastructureKafkaException(
                            "Failed to publish PurchaseOrderTransmitted event for order: " + event.getOrderId(),
                            ex
                    );
                } else {
                    System.out.println("Successfully published PurchaseOrderTransmitted event for order: " + event.getOrderId());
                }
            });
        } catch (Exception e) {
            throw new InfrastructureKafkaException(
                    "Failed to publish PurchaseOrderTransmitted event for order: " + event.getOrderId(),
                    e
            );
        }
    }
}
