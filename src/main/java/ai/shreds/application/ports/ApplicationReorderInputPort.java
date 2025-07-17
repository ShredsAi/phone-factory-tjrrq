package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedReorderRequestEventDTO;

/**
 * Input port for processing inventory reorder requests.
 */
public interface ApplicationReorderInputPort {

    /**
     * Process a reorder request event from Kafka.
     * @param reorderData Reorder request event data
     */
    void processReorderRequest(SharedReorderRequestEventDTO reorderData);
}