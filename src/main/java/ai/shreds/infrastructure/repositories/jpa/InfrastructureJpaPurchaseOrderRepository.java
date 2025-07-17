package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.repositories.entities.InfrastructureJpaPurchaseOrderEntity;
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
public interface InfrastructureJpaPurchaseOrderRepository extends JpaRepository<InfrastructureJpaPurchaseOrderEntity, String> {
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.status = :status ORDER BY p.orderDate DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findByStatus(@Param("status") String status);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.supplierId = :supplierId ORDER BY p.orderDate DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findBySupplierId(@Param("supplierId") String supplierId);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.status = :status AND p.supplierId = :supplierId ORDER BY p.orderDate DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findByStatusAndSupplierId(@Param("status") String status, @Param("supplierId") String supplierId);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.orderDate BETWEEN :startDate AND :endDate ORDER BY p.orderDate DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findByOrderDateBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.totalAmount >= :minAmount AND p.totalAmount <= :maxAmount ORDER BY p.totalAmount DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.expectedDeliveryDate <= :deliveryDate AND p.status IN ('SENT', 'ACKNOWLEDGED') ORDER BY p.expectedDeliveryDate ASC")
    List<InfrastructureJpaPurchaseOrderEntity> findByExpectedDeliveryDateLessThanEqual(@Param("deliveryDate") Timestamp deliveryDate);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.status = :status AND p.expectedDeliveryDate BETWEEN :startDate AND :endDate ORDER BY p.expectedDeliveryDate ASC")
    List<InfrastructureJpaPurchaseOrderEntity> findByStatusAndExpectedDeliveryDateBetween(@Param("status") String status, @Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
    
    @Query("SELECT COUNT(p) FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.supplierId = :supplierId")
    long countBySupplierId(@Param("supplierId") String supplierId);
    
    @Query("SELECT COUNT(p) FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.status = :status")
    long countByStatus(@Param("status") String status);
    
    @Query("SELECT SUM(p.totalAmount) FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.supplierId = :supplierId AND p.status = :status")
    BigDecimal sumTotalAmountBySupplierIdAndStatus(@Param("supplierId") String supplierId, @Param("status") String status);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.currency = :currency ORDER BY p.orderDate DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findByCurrency(@Param("currency") String currency);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.orderId LIKE :orderIdPattern ORDER BY p.orderDate DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findByOrderIdLike(@Param("orderIdPattern") String orderIdPattern);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.supplierId = :supplierId AND p.status = :status")
    boolean existsBySupplierIdAndStatus(@Param("supplierId") String supplierId, @Param("status") String status);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.paymentTerms = :paymentTerms ORDER BY p.orderDate DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findByPaymentTerms(@Param("paymentTerms") String paymentTerms);
    
    @Query("SELECT p FROM InfrastructureJpaPurchaseOrderEntity p WHERE p.deliveryConditions = :deliveryConditions ORDER BY p.orderDate DESC")
    List<InfrastructureJpaPurchaseOrderEntity> findByDeliveryConditions(@Param("deliveryConditions") String deliveryConditions);
}