package ai.shreds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InfrastructureApprovalStepRepository extends JpaRepository<InfrastructureApprovalStepJpaEntity, UUID> {
    
    @Query("SELECT s FROM InfrastructureApprovalStepJpaEntity s WHERE s.workflow.workflowId = :workflowId ORDER BY s.decisionTimestamp ASC")
    List<InfrastructureApprovalStepJpaEntity> findByWorkflowId(@Param("workflowId") UUID workflowId);
    
    @Query("SELECT s FROM InfrastructureApprovalStepJpaEntity s WHERE s.approverId = :approverId ORDER BY s.decisionTimestamp DESC")
    List<InfrastructureApprovalStepJpaEntity> findByApproverId(@Param("approverId") String approverId);
    
    @Query("SELECT s FROM InfrastructureApprovalStepJpaEntity s WHERE s.decision = :decision ORDER BY s.decisionTimestamp DESC")
    List<InfrastructureApprovalStepJpaEntity> findByDecision(@Param("decision") String decision);
    
    @Query("SELECT s FROM InfrastructureApprovalStepJpaEntity s WHERE s.approvalLevel = :level ORDER BY s.decisionTimestamp DESC")
    List<InfrastructureApprovalStepJpaEntity> findByApprovalLevel(@Param("level") String level);
    
    @Query("SELECT s FROM InfrastructureApprovalStepJpaEntity s WHERE s.workflow.workflowId = :workflowId AND s.approvalLevel = :level")
    List<InfrastructureApprovalStepJpaEntity> findByWorkflowIdAndApprovalLevel(@Param("workflowId") UUID workflowId, @Param("level") String level);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM InfrastructureApprovalStepJpaEntity s WHERE s.workflow.workflowId = :workflowId")
    boolean existsByWorkflowId(@Param("workflowId") UUID workflowId);
    
    @Query("SELECT s FROM InfrastructureApprovalStepJpaEntity s WHERE s.workflow.workflowId = :workflowId ORDER BY s.decisionTimestamp DESC")
    List<InfrastructureApprovalStepJpaEntity> findByWorkflowIdOrderByDecisionTimestampDesc(@Param("workflowId") UUID workflowId);
    
    @Query("SELECT s FROM InfrastructureApprovalStepJpaEntity s WHERE s.workflow.purchaseOrderId = :purchaseOrderId ORDER BY s.decisionTimestamp ASC")
    List<InfrastructureApprovalStepJpaEntity> findByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
}