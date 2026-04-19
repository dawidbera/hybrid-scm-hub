package com.scm.hub.infrastructure.adapter.persistence;

import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.domain.port.OrderPort;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.mapper.OrderMapper;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.OrderRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence adapter for order operations.
 * Connects the domain layer {@link OrderPort} to the database repositories.
 * Handles mapping between {@link Order} domain models and {@link OrderEntity} database entities.
 */
@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPort {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final SyncLogRepository syncLogRepository;

    /**
     * {@inheritDoc}
     * Behavior: 
     * 1. Maps domain order to entity and persists it.
     * 2. Automatically creates a synchronization log entry with "PENDING" status to trigger cloud synchronization.
     */
    @Override
    @Transactional
    public Order saveOrder(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        entity = orderRepository.save(entity);
        Order result = orderMapper.toDomain(entity);

        // Create sync log for cloud synchronization
        SyncLogEntity syncLog = SyncLogEntity.builder()
                .entityName("Order")
                .entityId(entity.getId())
                .status("PENDING")
                .build();
        syncLogRepository.save(syncLog);

        return result;
    }

    /**
     * {@inheritDoc}
     * @param id Unique identifier of the order.
     * @return The order domain model if found, or null.
     */
    @Override
    public Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDomain)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     * @return List of all orders in the system.
     */
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Behavior: Updates the order status and creates a new synchronization log entry for cloud update.
     * @param id ID of the order to update.
     * @param status New status to apply.
     * @return The updated order domain model.
     */
    @Override
    @Transactional
    public Order updateOrderStatus(UUID id, OrderStatus status) {
        OrderEntity entity = orderRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setStatus(status);
            entity.setUpdatedAt(java.time.LocalDateTime.now());
            entity = orderRepository.save(entity);

            SyncLogEntity syncLog = SyncLogEntity.builder()
                    .entityName("Order")
                    .entityId(entity.getId())
                    .status("PENDING")
                    .syncTimestamp(java.time.LocalDateTime.now())
                    .build();
            syncLogRepository.save(syncLog);

            return orderMapper.toDomain(entity);
        }
        return null;
    }
}
