package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.domain.model.Order;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderEntity;
import com.scm.hub.infrastructure.adapter.persistence.mapper.OrderMapper;
import com.scm.hub.infrastructure.adapter.persistence.repository.cloud.CloudOrderRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener that consumes order events from SQS and synchronizes them to the Cloud database.
 * This component acts as the 'Cloud-side' receiver in the event-driven sync architecture.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@org.springframework.context.annotation.Profile("!test")
public class OrderCloudSyncListener {

    private final CloudOrderRepository cloudOrderRepository;
    private final OrderMapper orderMapper;

    /**
     * Listens to the order events queue.
     * When a new order event is received, it maps the domain model to an entity
     * and persists it into the Cloud database instance.
     *
     * @param order The order received from the SQS queue.
     */
    @SqsListener("${scm.aws.sqs.queue-name}")
    @Transactional("cloudTransactionManager")
    public void onOrderEvent(Order order) {
        log.info("Received order event for synchronization: ID {}", order.getId());
        try {
            OrderEntity entity = orderMapper.toEntity(order);
            
            // Ensure child items are linked to the parent entity for JPA persistence
            if (entity.getItems() != null) {
                entity.getItems().forEach(item -> item.setOrder(entity));
            }
            
            cloudOrderRepository.save(entity);
            log.info("Successfully synchronized order {} to Cloud database", order.getId());
        } catch (Exception e) {
            log.error("Failed to synchronize order {} to Cloud database", order.getId(), e);
            // In a production system, we would send this to a Dead Letter Queue (DLQ)
            throw e; 
        }
    }
}
