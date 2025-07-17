package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainPurchaseOrderAggregate;
import ai.shreds.domain.ports.DomainOutputPortPurchaseOrderRepository;
import ai.shreds.domain.value_objects.DomainOrderStatus;
import ai.shreds.infrastructure.InfrastructureEntityMapper;
import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional
public class InfrastructurePurchaseOrderRepositoryImpl implements DomainOutputPortPurchaseOrderRepository {
    
    private final EntityManager entityManager;
    private final InfrastructurePurchaseOrderRepository jpaRepository;
    private final InfrastructureEntityMapper entityMapper;
    
    @Autowired
    public InfrastructurePurchaseOrderRepositoryImpl(
            EntityManager entityManager,
            InfrastructurePurchaseOrderRepository jpaRepository,
            InfrastructureEntityMapper entityMapper) {
        this.entityManager = entityManager;
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }
    
    @Override
    public DomainPurchaseOrderAggregate save(DomainPurchaseOrderAggregate order) {
        try {
            InfrastructurePurchaseOrderJpaEntity jpaEntity = entityMapper.toJpaPurchaseOrder(order);
            InfrastructurePurchaseOrderJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return entityMapper.toDomainPurchaseOrder(savedEntity);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to save purchase order: " + order.getOrderId().getValue(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public DomainPurchaseOrderAggregate findById(String orderId) {
        try {
            Optional<InfrastructurePurchaseOrderJpaEntity> jpaEntity = jpaRepository.findById(orderId);
            if (jpaEntity.isPresent()) {
                return entityMapper.toDomainPurchaseOrder(jpaEntity.get());
            }
            return null;
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to find purchase order by ID: " + orderId, e);
        }
    }
    
    @Override
    public DomainPurchaseOrderAggregate update(DomainPurchaseOrderAggregate order) {
        try {
            String orderId = order.getOrderId().getValue();
            if (!jpaRepository.existsById(orderId)) {
                throw new InfrastructureRepositoryException("Purchase order not found for update: " + orderId);
            }
            
            InfrastructurePurchaseOrderJpaEntity jpaEntity = entityMapper.toJpaPurchaseOrder(order);
            InfrastructurePurchaseOrderJpaEntity updatedEntity = jpaRepository.save(jpaEntity);
            return entityMapper.toDomainPurchaseOrder(updatedEntity);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to update purchase order: " + order.getOrderId().getValue(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DomainPurchaseOrderAggregate> findByStatus(DomainOrderStatus status) {
        try {
            List<InfrastructurePurchaseOrderJpaEntity> jpaEntities = jpaRepository.findByStatus(status.name());
            return jpaEntities.stream()
                    .map(entityMapper::toDomainPurchaseOrder)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to find purchase orders by status: " + status.name(), e);
        }
    }
}