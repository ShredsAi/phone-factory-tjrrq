package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainApprovalWorkflowEntity;
import ai.shreds.domain.ports.DomainOutputPortWorkflowRepository;
import ai.shreds.infrastructure.InfrastructureEntityMapper;
import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class InfrastructureWorkflowRepositoryImpl implements DomainOutputPortWorkflowRepository {

    private final EntityManager entityManager;
    private final InfrastructureApprovalWorkflowRepository jpaRepository;
    private final InfrastructureEntityMapper entityMapper;
    
    @Autowired
    public InfrastructureWorkflowRepositoryImpl(
            EntityManager entityManager,
            InfrastructureApprovalWorkflowRepository jpaRepository,
            InfrastructureEntityMapper entityMapper) {
        this.entityManager = entityManager;
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }
    
    @Override
    public DomainApprovalWorkflowEntity save(DomainApprovalWorkflowEntity workflow) {
        try {
            InfrastructureApprovalWorkflowJpaEntity jpaEntity = entityMapper.toJpaWorkflow(workflow);
            InfrastructureApprovalWorkflowJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return entityMapper.toDomainWorkflow(savedEntity);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                "Failed to save approval workflow: " + workflow.getWorkflowId(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public DomainApprovalWorkflowEntity findByPurchaseOrderId(String orderId) {
        try {
            Optional<InfrastructureApprovalWorkflowJpaEntity> jpaEntity = jpaRepository.findByPurchaseOrderId(orderId);
            if (jpaEntity.isPresent()) {
                return entityMapper.toDomainWorkflow(jpaEntity.get());
            }
            return null;
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                "Failed to find approval workflow by order ID: " + orderId, e);
        }
    }
    
    @Override
    public DomainApprovalWorkflowEntity update(DomainApprovalWorkflowEntity workflow) {
        try {
            UUID workflowId = workflow.getWorkflowId();
            if (!jpaRepository.existsById(workflowId)) {
                throw new InfrastructureRepositoryException(
                    "Approval workflow not found for update: " + workflowId);
            }
            
            InfrastructureApprovalWorkflowJpaEntity jpaEntity = entityMapper.toJpaWorkflow(workflow);
            InfrastructureApprovalWorkflowJpaEntity updatedEntity = jpaRepository.save(jpaEntity);
            return entityMapper.toDomainWorkflow(updatedEntity);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                "Failed to update approval workflow: " + workflow.getWorkflowId(), e);
        }
    }
}