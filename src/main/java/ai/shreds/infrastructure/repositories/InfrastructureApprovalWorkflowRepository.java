package ai.shreds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InfrastructureApprovalWorkflowRepository extends JpaRepository<InfrastructureApprovalWorkflowJpaEntity, UUID> {
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.purchaseOrderId = :purchaseOrderId")
    Optional<InfrastructureApprovalWorkflowJpaEntity> findByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
    
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.purchaseOrderId = :purchaseOrderId")
    boolean existsByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.workflowStatus = :status")
    List<InfrastructureApprovalWorkflowJpaEntity> findByWorkflowStatus(@Param("status") String status);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.currentApproverId = :approverId")
    List<InfrastructureApprovalWorkflowJpaEntity> findByCurrentApproverId(@Param("approverId") String approverId);
    
    @Query("SELECT w FROM InfrastructureApprovalWorkflowJpaEntity w WHERE w.currentApprovalLevel = :level")
    List<InfrastructureApprovalWorkflowJpaEntity> findByCurrentApprovalLevel(@Param("level") String level);
}