package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedModificationRequestDTO;
import ai.shreds.shared.dtos.SharedModificationResponseDTO;

public interface ApplicationModificationInputPort {

    /**
     * Modify an existing purchase order.
     * @param orderId identifier of the order to modify
     * @param modificationData modification details
     * @return result of the modification containing status and reapproval requirement
     */
    SharedModificationResponseDTO modifyOrder(String orderId, SharedModificationRequestDTO modificationData);
}