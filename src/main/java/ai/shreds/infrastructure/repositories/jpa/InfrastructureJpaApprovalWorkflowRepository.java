package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.repositories.InfrastructureApprovalWorkflowJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository interface for Approval Workflow entities.
 * Provides additional query methods beyond the basic CRUD operations.
 */
@Repository
public interface InfrastructureJpaApprovalWorkflowRepository extends JpaRepository<InfrastructureApprovalWorkflowJpaEntity, UUID> {
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.purchaseOrderId = :purchaseOrderId")
    Optional<InfrastructureApprovalWorkflowJpaEntity> findByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
    
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.purchaseOrderId = :purchaseOrderId")
    boolean existsByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = :status ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByWorkflowStatus(@Param("status") String status);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.currentApproverId = :approverId ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByCurrentApproverId(@Param("approverId") String approverId);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.currentApprovalLevel = :level ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByCurrentApprovalLevel(@Param("level") String level);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.requiredApprovalLevel = :level ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByRequiredApprovalLevel(@Param("level") String level);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = :status AND w.currentApproverId = :approverId ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByWorkflowStatusAndCurrentApproverId(@Param("status") String status, @Param("approverId") String approverId);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = :status AND w.currentApprovalLevel = :level ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByWorkflowStatusAndCurrentApprovalLevel(@Param("status") String status, @Param("level") String level);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.currentApprovalLevel = :level AND w.currentApproverId = :approverId ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByCurrentApprovalLevelAndCurrentApproverId(@Param("level") String level, @Param("approverId") String approverId);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus IN :statuses ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByWorkflowStatusIn(@Param("statuses") List<String> statuses);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.currentApprovalLevel IN :levels ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByCurrentApprovalLevelIn(@Param("levels") List<String> levels);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.requiredApprovalLevel IN :levels ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByRequiredApprovalLevelIn(@Param("levels") List<String> levels);
    
    @Query("SELECT COUNT(w) FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = :status")
    long countByWorkflowStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(w) FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.currentApproverId = :approverId")
    long countByCurrentApproverId(@Param("approverId") String approverId);
    
    @Query("SELECT COUNT(w) FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.currentApprovalLevel = :level")
    long countByCurrentApprovalLevel(@Param("level") String level);
    
    @Query("SELECT COUNT(w) FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = :status AND w.currentApproverId = :approverId")
    long countByWorkflowStatusAndCurrentApproverId(@Param("status") String status, @Param("approverId") String approverId);
    
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = :status AND w.currentApproverId = :approverId")
    boolean existsByWorkflowStatusAndCurrentApproverId(@Param("status") String status, @Param("approverId") String approverId);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = 'PENDING' AND w.currentApproverId IS NOT NULL ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findPendingWorkflowsWithApprover();
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = 'IN_PROGRESS' AND w.currentApproverId IS NOT NULL ORDER BY w.workflowId")
    List<InfrastructureApprovalWorkflowJpaEntity> findInProgressWorkflowsWithApprover();
    
    @Query("DELETE FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.purchaseOrderId = :purchaseOrderId")
    void deleteByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
}