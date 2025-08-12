package ai.shreds.infrastructure;

import ai.shreds.domain.entities.DomainApprovalStepEntity;
import ai.shreds.domain.entities.DomainApprovalWorkflowEntity;
import ai.shreds.domain.entities.DomainPurchaseOrderAggregate;
import ai.shreds.domain.value_objects.DomainApprovalDecision;
import ai.shreds.domain.value_objects.DomainApprovalLevel;
import ai.shreds.domain.value_objects.DomainMaterialId;
import ai.shreds.domain.value_objects.DomainMonetaryAmount;
import ai.shreds.domain.value_objects.DomainOrderLineItem;
import ai.shreds.domain.value_objects.DomainOrderStatus;
import ai.shreds.domain.value_objects.DomainPurchaseOrderId;
import ai.shreds.domain.value_objects.DomainSupplierId;
import ai.shreds.infrastructure.repositories.InfrastructureApprovalStepJpaEntity;
import ai.shreds.infrastructure.repositories.InfrastructureApprovalWorkflowJpaEntity;
import ai.shreds.infrastructure.repositories.InfrastructureOrderLineItemJpaEntity;
import ai.shreds.infrastructure.repositories.InfrastructurePurchaseOrderJpaEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class InfrastructureEntityMapper {

    // Purchase Order Aggregate Mapping
    public DomainPurchaseOrderAggregate toDomainPurchaseOrder(InfrastructurePurchaseOrderJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        DomainPurchaseOrderId orderId = new DomainPurchaseOrderId(jpaEntity.getOrderId());
        DomainSupplierId supplierId = new DomainSupplierId(jpaEntity.getSupplierId());
        LocalDateTime orderDate = jpaEntity.getOrderDate().toLocalDateTime();
        DomainOrderStatus status = DomainOrderStatus.valueOf(jpaEntity.getStatus());

        // Map line items
        List<DomainOrderLineItem> lineItems = toDomainLineItems(jpaEntity.getLineItems());

        // Map total amount
        DomainMonetaryAmount totalAmount = new DomainMonetaryAmount(
            jpaEntity.getTotalAmount(),
            jpaEntity.getCurrency()
        );

        // Map expected delivery date
        LocalDateTime expectedDeliveryDate = jpaEntity.getExpectedDeliveryDate() != null
            ? jpaEntity.getExpectedDeliveryDate().toLocalDateTime() : null;

        String paymentTerms = jpaEntity.getPaymentTerms();
        String deliveryConditions = jpaEntity.getDeliveryConditions();
        String contractualObligations = jpaEntity.getContractualObligations();

        // Construct aggregate with all required fields
        return new DomainPurchaseOrderAggregate(
            orderId,
            supplierId,
            orderDate,
            status,
            lineItems,
            totalAmount,
            expectedDeliveryDate,
            paymentTerms,
            deliveryConditions,
            contractualObligations
        );
    }

    public InfrastructurePurchaseOrderJpaEntity toJpaPurchaseOrder(DomainPurchaseOrderAggregate domainAggregate) {
        if (domainAggregate == null) {
            return null;
        }

        InfrastructurePurchaseOrderJpaEntity jpaEntity = new InfrastructurePurchaseOrderJpaEntity();
        jpaEntity.setOrderId(domainAggregate.getOrderId().getValue());
        jpaEntity.setSupplierId(domainAggregate.getSupplierId().getValue());
        jpaEntity.setOrderDate(Timestamp.valueOf(domainAggregate.getOrderDate()));
        jpaEntity.setStatus(domainAggregate.getStatus().name());
        jpaEntity.setTotalAmount(domainAggregate.getTotalAmount().getAmount());
        jpaEntity.setCurrency(domainAggregate.getTotalAmount().getCurrency());
        jpaEntity.setPaymentTerms(domainAggregate.getPaymentTerms());
        jpaEntity.setDeliveryConditions(domainAggregate.getDeliveryConditions());
        jpaEntity.setContractualObligations(domainAggregate.getContractualObligations());

        if (domainAggregate.getExpectedDeliveryDate() != null) {
            jpaEntity.setExpectedDeliveryDate(Timestamp.valueOf(domainAggregate.getExpectedDeliveryDate()));
        }

        // Map line items
        List<InfrastructureOrderLineItemJpaEntity> lineItems = toJpaLineItems(domainAggregate.getLineItems());
        jpaEntity.setLineItems(lineItems);
        for (InfrastructureOrderLineItemJpaEntity lineItem : lineItems) {
            lineItem.setPurchaseOrder(jpaEntity);
        }

        return jpaEntity;
    }

    // Line Item Mapping
    public List<DomainOrderLineItem> toDomainLineItems(List<InfrastructureOrderLineItemJpaEntity> jpaLineItems) {
        if (jpaLineItems == null || jpaLineItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<DomainOrderLineItem> domainLineItems = new ArrayList<>();
        for (InfrastructureOrderLineItemJpaEntity jpaLineItem : jpaLineItems) {
            domainLineItems.add(toDomainLineItem(jpaLineItem));
        }
        return domainLineItems;
    }

    public DomainOrderLineItem toDomainLineItem(InfrastructureOrderLineItemJpaEntity jpaLineItem) {
        if (jpaLineItem == null) {
            return null;
        }

        DomainMaterialId materialId = new DomainMaterialId(jpaLineItem.getMaterialId());
        DomainMonetaryAmount unitPrice = new DomainMonetaryAmount(jpaLineItem.getUnitPrice(), jpaLineItem.getCurrency());
        LocalDateTime requestedDeliveryDate = jpaLineItem.getRequestedDeliveryDate() != null ?
            jpaLineItem.getRequestedDeliveryDate().toLocalDateTime() : null;

        return new DomainOrderLineItem(
            jpaLineItem.getLineItemId(),
            materialId,
            jpaLineItem.getQuantity(),
            unitPrice,
            requestedDeliveryDate,
            jpaLineItem.getNotes()
        );
    }

    public List<InfrastructureOrderLineItemJpaEntity> toJpaLineItems(List<DomainOrderLineItem> domainLineItems) {
        if (domainLineItems == null || domainLineItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<InfrastructureOrderLineItemJpaEntity> jpaLineItems = new ArrayList<>();
        for (DomainOrderLineItem domainLineItem : domainLineItems) {
            jpaLineItems.add(toJpaLineItem(domainLineItem));
        }
        return jpaLineItems;
    }

    public InfrastructureOrderLineItemJpaEntity toJpaLineItem(DomainOrderLineItem domainLineItem) {
        if (domainLineItem == null) {
            return null;
        }

        InfrastructureOrderLineItemJpaEntity jpaLineItem = new InfrastructureOrderLineItemJpaEntity();
        jpaLineItem.setLineItemId(domainLineItem.getLineItemId());
        jpaLineItem.setMaterialId(domainLineItem.getMaterialId().getValue());
        jpaLineItem.setQuantity(domainLineItem.getQuantity());
        jpaLineItem.setUnitPrice(domainLineItem.getUnitPrice().getAmount());
        jpaLineItem.setCurrency(domainLineItem.getUnitPrice().getCurrency());
        jpaLineItem.setLineTotal(domainLineItem.calculateLineTotal().getAmount());
        jpaLineItem.setNotes(domainLineItem.getNotes());

        if (domainLineItem.getRequestedDeliveryDate() != null) {
            jpaLineItem.setRequestedDeliveryDate(Timestamp.valueOf(domainLineItem.getRequestedDeliveryDate()));
        }

        return jpaLineItem;
    }

    // Workflow Mapping
    public DomainApprovalWorkflowEntity toDomainWorkflow(InfrastructureApprovalWorkflowJpaEntity jpaWorkflow) {
        if (jpaWorkflow == null) {
            return null;
        }

        DomainPurchaseOrderId purchaseOrderId = new DomainPurchaseOrderId(jpaWorkflow.getPurchaseOrderId());
        DomainApprovalLevel requiredApprovalLevel = DomainApprovalLevel.valueOf(jpaWorkflow.getRequiredApprovalLevel());

        DomainApprovalWorkflowEntity workflow = new DomainApprovalWorkflowEntity(
            jpaWorkflow.getWorkflowId(),
            purchaseOrderId,
            requiredApprovalLevel
        );

        if (jpaWorkflow.getCurrentApprovalLevel() != null && jpaWorkflow.getCurrentApproverId() != null) {
            workflow.initiateWorkflow(
                DomainApprovalLevel.valueOf(jpaWorkflow.getCurrentApprovalLevel()),
                jpaWorkflow.getCurrentApproverId()
            );
        }

        List<DomainApprovalStepEntity> approvalSteps = toDomainApprovalSteps(jpaWorkflow.getApprovalSteps());
        for (DomainApprovalStepEntity step : approvalSteps) {
            workflow.advanceWorkflow(step.getDecision(), step.getApproverId(), step.getComments());
        }

        return workflow;
    }

    public InfrastructureApprovalWorkflowJpaEntity toJpaWorkflow(DomainApprovalWorkflowEntity domainWorkflow) {
        if (domainWorkflow == null) {
            return null;
        }

        InfrastructureApprovalWorkflowJpaEntity jpaWorkflow = new InfrastructureApprovalWorkflowJpaEntity();
        jpaWorkflow.setWorkflowId(domainWorkflow.getWorkflowId());
        jpaWorkflow.setPurchaseOrderId(domainWorkflow.getPurchaseOrderId().getValue());
        jpaWorkflow.setRequiredApprovalLevel(domainWorkflow.getRequiredApprovalLevel().name());
        jpaWorkflow.setWorkflowStatus(domainWorkflow.getWorkflowStatus().name());
        jpaWorkflow.setCurrentApproverId(domainWorkflow.getCurrentApproverId());

        if (domainWorkflow.getCurrentApprovalLevel() != null) {
            jpaWorkflow.setCurrentApprovalLevel(domainWorkflow.getCurrentApprovalLevel().name());
        }

        List<InfrastructureApprovalStepJpaEntity> approvalSteps = toJpaApprovalSteps(domainWorkflow.getApprovalSteps());
        jpaWorkflow.setApprovalSteps(approvalSteps);
        for (InfrastructureApprovalStepJpaEntity step : approvalSteps) {
            step.setWorkflow(jpaWorkflow);
        }

        return jpaWorkflow;
    }

    // Approval Step Mapping
    public List<DomainApprovalStepEntity> toDomainApprovalSteps(List<InfrastructureApprovalStepJpaEntity> jpaSteps) {
        if (jpaSteps == null || jpaSteps.isEmpty()) {
            return new ArrayList<>();
        }

        List<DomainApprovalStepEntity> domainSteps = new ArrayList<>();
        for (InfrastructureApprovalStepJpaEntity jpaStep : jpaSteps) {
            domainSteps.add(toDomainApprovalStep(jpaStep));
        }
        return domainSteps;
    }

    public DomainApprovalStepEntity toDomainApprovalStep(InfrastructureApprovalStepJpaEntity jpaStep) {
        if (jpaStep == null) {
            return null;
        }

        return new DomainApprovalStepEntity(
            jpaStep.getStepId(),
            jpaStep.getWorkflow().getWorkflowId(),
            jpaStep.getApprovalLevel(),
            jpaStep.getApproverId(),
            DomainApprovalDecision.valueOf(jpaStep.getDecision()),
            jpaStep.getComments(),
            jpaStep.getDecisionTimestamp().toLocalDateTime()
        );
    }

    public List<InfrastructureApprovalStepJpaEntity> toJpaApprovalSteps(List<DomainApprovalStepEntity> domainSteps) {
        if (domainSteps == null || domainSteps.isEmpty()) {
            return new ArrayList<>();
        }

        List<InfrastructureApprovalStepJpaEntity> jpaSteps = new ArrayList<>();
        for (DomainApprovalStepEntity domainStep : domainSteps) {
            jpaSteps.add(toJpaApprovalStep(domainStep));
        }
        return jpaSteps;
    }

    public InfrastructureApprovalStepJpaEntity toJpaApprovalStep(DomainApprovalStepEntity domainStep) {
        if (domainStep == null) {
            return null;
        }

        InfrastructureApprovalStepJpaEntity jpaStep = new InfrastructureApprovalStepJpaEntity();
        jpaStep.setStepId(domainStep.getStepId());
        jpaStep.setApprovalLevel(domainStep.getApprovalLevel());
        jpaStep.setApproverId(domainStep.getApproverId());
        jpaStep.setDecision(domainStep.getDecision().name());
        jpaStep.setComments(domainStep.getComments());
        jpaStep.setDecisionTimestamp(Timestamp.valueOf(domainStep.getDecisionTimestamp()));

        return jpaStep;
    }
}
