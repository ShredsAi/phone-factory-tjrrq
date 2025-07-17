package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedSupplierAcknowledgmentRequestDTO;

public interface ApplicationAcknowledgmentInputPort {

    /**
     * Process an incoming supplier acknowledgment.
     * @param acknowledgmentData acknowledgment details
     */
    void processAcknowledgment(SharedSupplierAcknowledgmentRequestDTO acknowledgmentData);
}