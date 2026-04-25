package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.base.AbstractIntegrationTest;
import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.OrderItemEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.OrderRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for verifying complex synchronization of Orders and their associated OrderItems.
 * Ensures that the entire order graph is correctly replicated from On-Premise to Cloud.
 */
class OrderSyncIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SyncService syncService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SyncLogRepository syncLogRepository;

    @PersistenceContext(unitName = "cloud")
    private EntityManager cloudEntityManager;

    /**
     * Verifies that an order and its items are correctly synchronized to the Cloud database.
     * Validates proper handling of relationships and database constraints.
     */
    @Test
    void shouldSyncOrderAndItemsToCloud() {
        // Given
        ProductEntity product = ProductEntity.builder()
                .sku("ORD-PROD-001")
                .name("Order Test Product")
                .basePrice(BigDecimal.valueOf(50.00))
                .build();
        product = productRepository.save(product);

        // Pre-sync product to cloud to avoid foreign key violation if the schema enforces it
        SyncLogEntity productSyncLog = SyncLogEntity.builder()
                .entityName("Product")
                .entityId(product.getId())
                .status(OrderStatus.PENDING)
                .syncTimestamp(LocalDateTime.now())
                .build();
        syncService.syncEntity(productSyncLog);

        OrderEntity order = OrderEntity.builder()
                .customerName("John Doe")
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        OrderItemEntity item = OrderItemEntity.builder()
                .productId(product.getId())
                .quantity(2)
                .price(100.00)
                .order(order)
                .build();
        
        order.setItems(List.of(item));
        order = orderRepository.save(order);

        SyncLogEntity orderSyncLog = SyncLogEntity.builder()
                .entityName("Order")
                .entityId(order.getId())
                .status(OrderStatus.PENDING)
                .syncTimestamp(LocalDateTime.now())
                .build();
        orderSyncLog = syncLogRepository.save(orderSyncLog);

        // When
        syncService.syncEntity(orderSyncLog);

        // Then
        SyncLogEntity updatedLog = syncLogRepository.findById(orderSyncLog.getId()).orElseThrow();
        assertThat(updatedLog.getStatus()).isEqualTo(OrderStatus.SUCCESS);

        OrderEntity cloudOrder = cloudEntityManager.createQuery(
                "SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id", OrderEntity.class)
                .setParameter("id", order.getId())
                .getSingleResult();
        assertThat(cloudOrder).isNotNull();
        assertThat(cloudOrder.getCustomerName()).isEqualTo("John Doe");
        assertThat(cloudOrder.getItems()).hasSize(1);
        assertThat(cloudOrder.getItems().get(0).getQuantity()).isEqualTo(2);
    }
}
