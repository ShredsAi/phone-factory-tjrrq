package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.DomainApprovalDecision;
import java.time.LocalDateTime;
import java.util.UUID;

public class DomainApprovalStepEntity {
    private final UUID stepId;
    private final UUID workflowId;
    private final String approvalLevel;
    private final String approverId;
    private final DomainApprovalDecision decision;
    private final String comments;
    private final LocalDateTime decisionTimestamp;

    public DomainApprovalStepEntity(UUID stepId,
                                    UUID workflowId,
                                    String approvalLevel,
                                    String approverId,
                                    DomainApprovalDecision decision,
                                    String comments,
                                    LocalDateTime decisionTimestamp) {
        if (stepId == null) {
            throw new IllegalArgumentException("stepId cannot be null");
        }
        if (workflowId == null) {
            throw new IllegalArgumentException("workflowId cannot be null");
        }
        if (approvalLevel == null || approvalLevel.isEmpty()) {
            throw new IllegalArgumentException("approvalLevel cannot be null or empty");
        }
        if (approverId == null || approverId.isEmpty()) {
            throw new IllegalArgumentException("approverId cannot be null or empty");
        }
        if (decision == null) {
            throw new IllegalArgumentException("decision cannot be null");
        }
        if (decisionTimestamp == null) {
            throw new IllegalArgumentException("decisionTimestamp cannot be null");
        }
        this.stepId = stepId;
        this.workflowId = workflowId;
        this.approvalLevel = approvalLevel;
        this.approverId = approverId;
        this.decision = decision;
        this.comments = comments;
        this.decisionTimestamp = decisionTimestamp;
    }

    public UUID getStepId() {
        return stepId;
    }

    public UUID getWorkflowId() {
        return workflowId;
    }

    public String getApprovalLevel() {
        return approvalLevel;
    }

    public String getApproverId() {
        return approverId;
    }

    public DomainApprovalDecision getDecision() {
        return decision;
    }

    public String getComments() {
        return comments;
    }

    public LocalDateTime getDecisionTimestamp() {
        return decisionTimestamp;
    }

    public boolean isApproved() {
        return DomainApprovalDecision.APPROVED.equals(decision);
    }
}
