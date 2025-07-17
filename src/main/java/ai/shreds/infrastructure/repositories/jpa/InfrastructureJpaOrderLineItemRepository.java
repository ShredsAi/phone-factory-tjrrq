package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.repositories.entities.InfrastructureJpaOrderLineItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * JPA Repository interface for Order Line Item entities.
 * Provides additional query methods beyond the basic CRUD operations.
 */
@Repository
public interface InfrastructureJpaOrderLineItemRepository extends JpaRepository<InfrastructureJpaOrderLineItemEntity, UUID> {
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.purchaseOrder.orderId = :purchaseOrderId ORDER BY li.lineItemId")
    List<InfrastructureJpaOrderLineItemEntity> findByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.materialId = :materialId ORDER BY li.quantity DESC")
    List<InfrastructureJpaOrderLineItemEntity> findByMaterialId(@Param("materialId") String materialId);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.quantity >= :minQuantity ORDER BY li.quantity DESC")
    List<InfrastructureJpaOrderLineItemEntity> findByQuantityGreaterThanEqual(@Param("minQuantity") BigDecimal minQuantity);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.unitPrice >= :minPrice AND li.unitPrice <= :maxPrice ORDER BY li.unitPrice DESC")
    List<InfrastructureJpaOrderLineItemEntity> findByUnitPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.lineTotal >= :minTotal AND li.lineTotal <= :maxTotal ORDER BY li.lineTotal DESC")
    List<InfrastructureJpaOrderLineItemEntity> findByLineTotalBetween(@Param("minTotal") BigDecimal minTotal, @Param("maxTotal") BigDecimal maxTotal);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.requestedDeliveryDate <= :deliveryDate ORDER BY li.requestedDeliveryDate ASC")
    List<InfrastructureJpaOrderLineItemEntity> findByRequestedDeliveryDateLessThanEqual(@Param("deliveryDate") Timestamp deliveryDate);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.requestedDeliveryDate BETWEEN :startDate AND :endDate ORDER BY li.requestedDeliveryDate ASC")
    List<InfrastructureJpaOrderLineItemEntity> findByRequestedDeliveryDateBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.currency = :currency ORDER BY li.lineTotal DESC")
    List<InfrastructureJpaOrderLineItemEntity> findByCurrency(@Param("currency") String currency);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.notes LIKE :notesPattern ORDER BY li.lineItemId")
    List<InfrastructureJpaOrderLineItemEntity> findByNotesLike(@Param("notesPattern") String notesPattern);
    
    @Query("SELECT COUNT(li) FROM InfrastructureJpaOrderLineItemEntity li WHERE li.purchaseOrder.orderId = :purchaseOrderId")
    long countByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
    
    @Query("SELECT COUNT(li) FROM InfrastructureJpaOrderLineItemEntity li WHERE li.materialId = :materialId")
    long countByMaterialId(@Param("materialId") String materialId);
    
    @Query("SELECT SUM(li.quantity) FROM InfrastructureJpaOrderLineItemEntity li WHERE li.materialId = :materialId")
    BigDecimal sumQuantityByMaterialId(@Param("materialId") String materialId);
    
    @Query("SELECT SUM(li.lineTotal) FROM InfrastructureJpaOrderLineItemEntity li WHERE li.purchaseOrder.orderId = :purchaseOrderId")
    BigDecimal sumLineTotalByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
    
    @Query("SELECT SUM(li.lineTotal) FROM InfrastructureJpaOrderLineItemEntity li WHERE li.materialId = :materialId")
    BigDecimal sumLineTotalByMaterialId(@Param("materialId") String materialId);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.purchaseOrder.orderId = :purchaseOrderId AND li.materialId = :materialId")
    List<InfrastructureJpaOrderLineItemEntity> findByPurchaseOrderIdAndMaterialId(@Param("purchaseOrderId") String purchaseOrderId, @Param("materialId") String materialId);
    
    @Query("SELECT CASE WHEN COUNT(li) > 0 THEN true ELSE false END FROM InfrastructureJpaOrderLineItemEntity li WHERE li.purchaseOrder.orderId = :purchaseOrderId AND li.materialId = :materialId")
    boolean existsByPurchaseOrderIdAndMaterialId(@Param("purchaseOrderId") String purchaseOrderId, @Param("materialId") String materialId);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.purchaseOrder.supplierId = :supplierId ORDER BY li.lineTotal DESC")
    List<InfrastructureJpaOrderLineItemEntity> findBySupplierIdOrderByLineTotalDesc(@Param("supplierId") String supplierId);
    
    @Query("SELECT li FROM InfrastructureJpaOrderLineItemEntity li WHERE li.purchaseOrder.status = :status ORDER BY li.requestedDeliveryDate ASC")
    List<InfrastructureJpaOrderLineItemEntity> findByPurchaseOrderStatusOrderByRequestedDeliveryDateAsc(@Param("status") String status);
    
    @Query("DELETE FROM InfrastructureJpaOrderLineItemEntity li WHERE li.purchaseOrder.orderId = :purchaseOrderId")
    void deleteByPurchaseOrderId(@Param("purchaseOrderId") String purchaseOrderId);
}