package ai.shreds.infrastructure.external_services;

import ai.shreds.application.ports.ApplicationKafkaOutputPort;
import ai.shreds.shared.dtos.SharedPurchaseOrderTransmittedEventDTO;
import ai.shreds.infrastructure.exceptions.InfrastructureKafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

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
            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    purchaseOrderTopic,
                    event.getOrderId(), // Use orderId as message key for partitioning
                    event
            );

            future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                @Override
                public void onSuccess(SendResult<String, Object> result) {
                    // Log success or handle success scenario
                    System.out.println("Successfully published PurchaseOrderTransmitted event for order: " + event.getOrderId());
                }

                @Override
                public void onFailure(Throwable ex) {
                    throw new InfrastructureKafkaException(
                            "Failed to publish PurchaseOrderTransmitted event for order: " + event.getOrderId(),
                            ex
                    );
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