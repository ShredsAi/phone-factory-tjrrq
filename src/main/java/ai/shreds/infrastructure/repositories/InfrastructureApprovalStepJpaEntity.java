package ai.shreds.infrastructure.repositories;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "approval_steps")
public class InfrastructureApprovalStepJpaEntity {
    
    @Id
    @Column(name = "step_id")
    private UUID stepId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private InfrastructureApprovalWorkflowJpaEntity workflow;
    
    @Column(name = "approval_level", length = 30, nullable = false)
    private String approvalLevel;
    
    @Column(name = "approver_id", length = 50, nullable = false)
    private String approverId;
    
    @Column(name = "decision", length = 20, nullable = false)
    private String decision;
    
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;
    
    @Column(name = "decision_timestamp", nullable = false)
    private Timestamp decisionTimestamp;
    
    // Default constructor
    public InfrastructureApprovalStepJpaEntity() {}
    
    // Constructor with required fields
    public InfrastructureApprovalStepJpaEntity(UUID stepId, String approvalLevel, String approverId,
                                             String decision, Timestamp decisionTimestamp) {
        this.stepId = stepId;
        this.approvalLevel = approvalLevel;
        this.approverId = approverId;
        this.decision = decision;
        this.decisionTimestamp = decisionTimestamp;
    }
    
    // Getters and setters
    public UUID getStepId() {
        return stepId;
    }
    
    public void setStepId(UUID stepId) {
        this.stepId = stepId;
    }
    
    public InfrastructureApprovalWorkflowJpaEntity getWorkflow() {
        return workflow;
    }
    
    public void setWorkflow(InfrastructureApprovalWorkflowJpaEntity workflow) {
        this.workflow = workflow;
    }
    
    public String getApprovalLevel() {
        return approvalLevel;
    }
    
    public void setApprovalLevel(String approvalLevel) {
        this.approvalLevel = approvalLevel;
    }
    
    public String getApproverId() {
        return approverId;
    }
    
    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }
    
    public String getDecision() {
        return decision;
    }
    
    public void setDecision(String decision) {
        this.decision = decision;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public Timestamp getDecisionTimestamp() {
        return decisionTimestamp;
    }
    
    public void setDecisionTimestamp(Timestamp decisionTimestamp) {
        this.decisionTimestamp = decisionTimestamp;
    }
}