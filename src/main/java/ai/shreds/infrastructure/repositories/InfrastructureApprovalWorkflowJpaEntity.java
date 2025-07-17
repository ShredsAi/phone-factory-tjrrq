package ai.shreds.infrastructure.repositories;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "approval_workflows")
public class InfrastructureApprovalWorkflowJpaEntity {
    
    @Id
    @Column(name = "workflow_id")
    private UUID workflowId;
    
    @Column(name = "purchase_order_id", length = 50, nullable = false, unique = true)
    private String purchaseOrderId;
    
    @Column(name = "current_approval_level", length = 30)
    private String currentApprovalLevel;
    
    @Column(name = "current_approver_id", length = 50)
    private String currentApproverId;
    
    @Column(name = "required_approval_level", length = 30, nullable = false)
    private String requiredApprovalLevel;
    
    @Column(name = "workflow_status", length = 30, nullable = false)
    private String workflowStatus;
    
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InfrastructureApprovalStepJpaEntity> approvalSteps = new ArrayList<>();
    
    // Default constructor
    public InfrastructureApprovalWorkflowJpaEntity() {}
    
    // Constructor with required fields
    public InfrastructureApprovalWorkflowJpaEntity(UUID workflowId, String purchaseOrderId,
                                                  String requiredApprovalLevel, String workflowStatus) {
        this.workflowId = workflowId;
        this.purchaseOrderId = purchaseOrderId;
        this.requiredApprovalLevel = requiredApprovalLevel;
        this.workflowStatus = workflowStatus;
    }
    
    // Getters and setters
    public UUID getWorkflowId() {
        return workflowId;
    }
    
    public void setWorkflowId(UUID workflowId) {
        this.workflowId = workflowId;
    }
    
    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }
    
    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }
    
    public String getCurrentApprovalLevel() {
        return currentApprovalLevel;
    }
    
    public void setCurrentApprovalLevel(String currentApprovalLevel) {
        this.currentApprovalLevel = currentApprovalLevel;
    }
    
    public String getCurrentApproverId() {
        return currentApproverId;
    }
    
    public void setCurrentApproverId(String currentApproverId) {
        this.currentApproverId = currentApproverId;
    }
    
    public String getRequiredApprovalLevel() {
        return requiredApprovalLevel;
    }
    
    public void setRequiredApprovalLevel(String requiredApprovalLevel) {
        this.requiredApprovalLevel = requiredApprovalLevel;
    }
    
    public String getWorkflowStatus() {
        return workflowStatus;
    }
    
    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }
    
    public List<InfrastructureApprovalStepJpaEntity> getApprovalSteps() {
        return approvalSteps;
    }
    
    public void setApprovalSteps(List<InfrastructureApprovalStepJpaEntity> approvalSteps) {
        this.approvalSteps = approvalSteps;
    }
    
    public void addApprovalStep(InfrastructureApprovalStepJpaEntity step) {
        approvalSteps.add(step);
        step.setWorkflow(this);
    }
    
    public void removeApprovalStep(InfrastructureApprovalStepJpaEntity step) {
        approvalSteps.remove(step);
        step.setWorkflow(null);
    }
}