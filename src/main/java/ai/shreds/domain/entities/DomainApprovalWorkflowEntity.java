package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainApprovalDecision;
import ai.shreds.domain.value_objects.DomainApprovalLevel;
import ai.shreds.domain.value_objects.DomainPurchaseOrderId;
import ai.shreds.domain.value_objects.DomainWorkflowStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DomainApprovalWorkflowEntity {
    private final UUID workflowId;
    private final DomainPurchaseOrderId purchaseOrderId;
    private DomainApprovalLevel currentApprovalLevel;
    private String currentApproverId;
    private final DomainApprovalLevel requiredApprovalLevel;
    private DomainWorkflowStatus workflowStatus;
    private final List<DomainApprovalStepEntity> approvalSteps;

    public DomainApprovalWorkflowEntity(UUID workflowId,
                                        DomainPurchaseOrderId purchaseOrderId,
                                        DomainApprovalLevel requiredApprovalLevel) {
        if (workflowId == null) {
            throw new IllegalArgumentException("workflowId cannot be null");
        }
        if (purchaseOrderId == null) {
            throw new IllegalArgumentException("purchaseOrderId cannot be null");
        }
        if (requiredApprovalLevel == null) {
            throw new IllegalArgumentException("requiredApprovalLevel cannot be null");
        }
        this.workflowId = workflowId;
        this.purchaseOrderId = purchaseOrderId;
        this.requiredApprovalLevel = requiredApprovalLevel;
        this.workflowStatus = DomainWorkflowStatus.PENDING;
        this.approvalSteps = new ArrayList<>();
    }

    public void initiateWorkflow(DomainApprovalLevel firstLevel, String firstApproverId) {
        this.currentApprovalLevel = firstLevel;
        this.currentApproverId = firstApproverId;
    }

    public void advanceWorkflow(DomainApprovalDecision decision, String approverId, String comments) {
        if (decision == null) {
            throw new IllegalArgumentException("decision cannot be null");
        }
        if (approverId == null || approverId.isEmpty()) {
            throw new IllegalArgumentException("approverId cannot be null or empty");
        }
        // Record the step
        DomainApprovalStepEntity step = new DomainApprovalStepEntity(
                UUID.randomUUID(),
                this.workflowId,
                this.currentApprovalLevel.name(),
                approverId,
                decision,
                comments,
                LocalDateTime.now()
        );
        this.approvalSteps.add(step);

        if (decision == DomainApprovalDecision.REJECTED) {
            this.workflowStatus = DomainWorkflowStatus.REJECTED;
            return;
        }
        if (decision == DomainApprovalDecision.APPROVED) {
            if (this.currentApprovalLevel == this.requiredApprovalLevel) {
                this.workflowStatus = DomainWorkflowStatus.APPROVED;
                return;
            }
            this.currentApprovalLevel = getNextApprovalLevel();
            this.currentApproverId = null; // to be assigned by orchestrator
            this.workflowStatus = DomainWorkflowStatus.IN_PROGRESS;
            return;
        }
        if (decision == DomainApprovalDecision.ESCALATED) {
            this.workflowStatus = DomainWorkflowStatus.IN_PROGRESS;
            this.currentApprovalLevel = this.requiredApprovalLevel;
            this.currentApproverId = null;
        }
    }

    public boolean isComplete() {
        return this.workflowStatus == DomainWorkflowStatus.APPROVED
                || this.workflowStatus == DomainWorkflowStatus.REJECTED;
    }

    public boolean requiresEscalation() {
        return approvalSteps.stream()
                .anyMatch(step -> step.getDecision() == DomainApprovalDecision.ESCALATED);
    }

    public DomainApprovalLevel getNextApprovalLevel() {
        DomainApprovalLevel[] levels = DomainApprovalLevel.values();
        for (int i = 0; i < levels.length - 1; i++) {
            if (levels[i] == this.currentApprovalLevel) {
                return levels[i + 1];
            }
        }
        return this.requiredApprovalLevel;
    }

    public UUID getWorkflowId() {
        return workflowId;
    }

    public DomainPurchaseOrderId getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public DomainApprovalLevel getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }

    public String getCurrentApproverId() {
        return currentApproverId;
    }

    public DomainApprovalLevel getRequiredApprovalLevel() {
        return requiredApprovalLevel;
    }

    public DomainWorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public List<DomainApprovalStepEntity> getApprovalSteps() {
        return new ArrayList<>(approvalSteps);
    }
}
