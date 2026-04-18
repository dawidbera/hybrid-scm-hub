package com.scm.hub.infrastructure.adapter.persistence;

import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderItem;
import com.scm.hub.domain.port.OrderPort;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderItemEntity;
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
 */
@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPort {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final SyncLogRepository syncLogRepository;

    @Override
    @Transactional
    public Order createOrder(Order order) {
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

    @Override
    public Order getOrderById(UUID id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Order updateOrderStatus(UUID id, String status) {
        OrderEntity entity = orderRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setStatus(status);
            entity.setUpdatedAt(java.time.LocalDateTime.now());
            entity = orderRepository.save(entity);
            return orderMapper.toDomain(entity);
        }
        return null;
    }
}