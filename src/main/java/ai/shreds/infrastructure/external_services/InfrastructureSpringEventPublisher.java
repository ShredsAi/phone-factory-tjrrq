package ai.shreds.infrastructure.external_services;

import ai.shreds.application.ports.ApplicationEventOutputPort;
import ai.shreds.shared.dtos.SharedPurchaseOrderCreatedEventDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class InfrastructureSpringEventPublisher implements ApplicationEventOutputPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    public InfrastructureSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publishPurchaseOrderCreated(SharedPurchaseOrderCreatedEventDTO event) {
        applicationEventPublisher.publishEvent(new PurchaseOrderCreatedEvent(event));
    }

    // Internal Spring event wrapper
    public static class PurchaseOrderCreatedEvent {
        private final SharedPurchaseOrderCreatedEventDTO eventData;

        public PurchaseOrderCreatedEvent(SharedPurchaseOrderCreatedEventDTO eventData) {
            this.eventData = eventData;
        }

        public SharedPurchaseOrderCreatedEventDTO getEventData() {
            return eventData;
        }
    }
}