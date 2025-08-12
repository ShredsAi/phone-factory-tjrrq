package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.repositories.InfrastructurePurchaseOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository interface for Purchase Order entities.
 * Provides additional query methods beyond the basic CRUD operations.
 */
@Repository
public interface InfrastructureJpaPurchaseOrderRepository extends JpaRepository<InfrastructurePurchaseOrderJpaEntity, String> {
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.status = :status ORDER BY p.orderDate DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findByStatus(@Param("status") String status);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.supplierId = :supplierId ORDER BY p.orderDate DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findBySupplierId(@Param("supplierId") String supplierId);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.status = :status AND p.supplierId = :supplierId ORDER BY p.orderDate DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findByStatusAndSupplierId(@Param("status") String status, @Param("supplierId") String supplierId);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.orderDate BETWEEN :startDate AND :endDate ORDER BY p.orderDate DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findByOrderDateBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.totalAmount >= :minAmount AND p.totalAmount <= :maxAmount ORDER BY p.totalAmount DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.expectedDeliveryDate <= :deliveryDate AND p.status IN ('SENT', 'ACKNOWLEDGED') ORDER BY p.expectedDeliveryDate ASC")
    List<InfrastructurePurchaseOrderJpaEntity> findByExpectedDeliveryDateLessThanEqual(@Param("deliveryDate") Timestamp deliveryDate);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.status = :status AND p.expectedDeliveryDate BETWEEN :startDate AND :endDate ORDER BY p.expectedDeliveryDate ASC")
    List<InfrastructurePurchaseOrderJpaEntity> findByStatusAndExpectedDeliveryDateBetween(@Param("status") String status, @Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
    
    @Query("SELECT COUNT(p) FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.supplierId = :supplierId")
    long countBySupplierId(@Param("supplierId") String supplierId);
    
    @Query("SELECT COUNT(p) FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.status = :status")
    long countByStatus(@Param("status") String status);
    
    @Query("SELECT SUM(p.totalAmount) FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.supplierId = :supplierId AND p.status = :status")
    BigDecimal sumTotalAmountBySupplierIdAndStatus(@Param("supplierId") String supplierId, @Param("status") String status);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.currency = :currency ORDER BY p.orderDate DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findByCurrency(@Param("currency") String currency);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.orderId LIKE :orderIdPattern ORDER BY p.orderDate DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findByOrderIdLike(@Param("orderIdPattern") String orderIdPattern);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.supplierId = :supplierId AND p.status = :status")
    boolean existsBySupplierIdAndStatus(@Param("supplierId") String supplierId, @Param("status") String status);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.paymentTerms = :paymentTerms ORDER BY p.orderDate DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findByPaymentTerms(@Param("paymentTerms") String paymentTerms);
    
    @Query("SELECT p FROM InfrastructurePurchaseOrderJpaEntity p WHERE p.deliveryConditions = :deliveryConditions ORDER BY p.orderDate DESC")
    List<InfrastructurePurchaseOrderJpaEntity> findByDeliveryConditions(@Param("deliveryConditions") String deliveryConditions);
}