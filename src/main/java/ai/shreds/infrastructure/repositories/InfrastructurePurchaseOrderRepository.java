package ai.shreds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfrastructurePurchaseOrderRepository extends JpaRepository<InfrastructurePurchaseOrderJpaEntity, String> {
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.status = :status")
    List<InfrastructurePurchaseOrderJpaEntity> findByStatus(@Param("status") String status);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.supplierId = :supplierId")
    List<InfrastructurePurchaseOrderJpaEntity> findBySupplierId(@Param("supplierId") String supplierId);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.status = :status AND p.supplierId = :supplierId")
    List<InfrastructurePurchaseOrderJpaEntity> findByStatusAndSupplierId(@Param("status") String status, @Param("supplierId") String supplierId);
}