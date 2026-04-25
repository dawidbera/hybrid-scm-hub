package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.base.AbstractIntegrationTest;
import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for verifying basic product synchronization logic.
 * Ensures that changes made to products in the On-Premise database are correctly pushed to the Cloud.
 */
class SyncIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SyncService syncService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SyncLogRepository syncLogRepository;

    @PersistenceContext(unitName = "cloud")
    private EntityManager cloudEntityManager;

    /**
     * Verifies that a product created in the On-Premise database is correctly synchronized to the Cloud database.
     */
    @Test
    void shouldSyncProductToCloud() {
        // Given
        ProductEntity product = ProductEntity.builder()
                .sku("TEST-SKU-001")
                .name("Integration Test Product")
                .description("Test Description")
                .basePrice(BigDecimal.valueOf(99.99))
                .build();
        product = productRepository.save(product);

        SyncLogEntity syncLog = SyncLogEntity.builder()
                .entityName("Product")
                .entityId(product.getId())
                .status(OrderStatus.PENDING)
                .syncTimestamp(LocalDateTime.now())
                .build();
        syncLog = syncLogRepository.save(syncLog);

        // When
        syncService.syncEntity(syncLog);

        // Then
        SyncLogEntity updatedLog = syncLogRepository.findById(syncLog.getId()).orElseThrow();
        assertThat(updatedLog.getStatus())
                .withFailMessage("Sync failed with error: " + updatedLog.getErrorMessage())
                .isEqualTo(OrderStatus.SUCCESS);

        ProductEntity cloudProduct = cloudEntityManager.find(ProductEntity.class, product.getId());
        assertThat(cloudProduct).isNotNull();
        assertThat(cloudProduct.getSku()).isEqualTo("TEST-SKU-001");
    }
}
