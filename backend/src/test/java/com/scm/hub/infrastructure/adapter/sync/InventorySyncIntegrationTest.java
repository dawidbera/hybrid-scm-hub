package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.base.AbstractIntegrationTest;
import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.StockEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.WarehouseEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.StockRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.WarehouseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for verifying synchronization of Warehouse and Stock entities.
 * Ensures that inventory levels and warehouse metadata are correctly pushed to the Cloud database.
 */
class InventorySyncIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SyncService syncService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private SyncLogRepository syncLogRepository;

    @PersistenceContext(unitName = "cloud")
    private EntityManager cloudEntityManager;

    /**
     * Verifies that warehouse information and stock levels are correctly synchronized to the Cloud.
     * Ensures consistent inventory state between On-Premise and Cloud environments.
     */
    @Test
    void shouldSyncWarehouseAndStockToCloud() {
        // Given
        ProductEntity product = ProductEntity.builder()
                .sku("INV-PROD-001")
                .name("Inventory Test Product")
                .basePrice(BigDecimal.valueOf(10.00))
                .build();
        product = productRepository.save(product);

        WarehouseEntity warehouse = WarehouseEntity.builder()
                .name("Central Warehouse")
                .location("Berlin")
                .build();
        warehouse = warehouseRepository.save(warehouse);

        StockEntity stock = StockEntity.builder()
                .productId(product.getId())
                .warehouseId(warehouse.getId())
                .quantity(500)
                .lastUpdated(LocalDateTime.now())
                .version(1L)
                .build();
        stock = stockRepository.save(stock);

        // Sync Product and Warehouse first
        syncService.syncEntity(SyncLogEntity.builder()
                .entityName("Product").entityId(product.getId()).status(OrderStatus.PENDING).build());
        syncService.syncEntity(SyncLogEntity.builder()
                .entityName("Warehouse").entityId(warehouse.getId()).status(OrderStatus.PENDING).build());

        SyncLogEntity stockSyncLog = SyncLogEntity.builder()
                .entityName("Stock")
                .entityId(stock.getId())
                .status(OrderStatus.PENDING)
                .syncTimestamp(LocalDateTime.now())
                .build();
        stockSyncLog = syncLogRepository.save(stockSyncLog);

        // When
        syncService.syncEntity(stockSyncLog);

        // Then
        SyncLogEntity updatedLog = syncLogRepository.findById(stockSyncLog.getId()).orElseThrow();
        assertThat(updatedLog.getStatus()).isEqualTo(OrderStatus.SUCCESS);

        StockEntity cloudStock = cloudEntityManager.find(StockEntity.class, stock.getId());
        assertThat(cloudStock).isNotNull();
        assertThat(cloudStock.getQuantity()).isEqualTo(500);
        assertThat(cloudStock.getWarehouseId()).isEqualTo(warehouse.getId());
    }
}
