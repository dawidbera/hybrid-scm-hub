package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.OrderRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.StockRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Internal component dedicated to executing operations on the Cloud database.
 * Uses JdbcTemplate for direct UPSERT operations to avoid Hibernate session/transaction issues
 * when synchronizing between different data sources.
 */
@Component
@RequiredArgsConstructor
public class CloudSyncProcessor {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final JdbcTemplate cloudJdbcTemplate;

    /**
     * Synchronizes a single entity state to the Cloud database.
     * Uses PostgreSQL native "ON CONFLICT" for atomic and robust synchronization.
     */
    public void pushToCloud(SyncLogEntity syncLog) {
        if ("Product".equals(syncLog.getEntityName())) {
            productRepository.findById(syncLog.getEntityId()).ifPresent(p -> {
                cloudJdbcTemplate.update(
                    "INSERT INTO products (id, sku, name, description, base_price) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET " +
                    "sku = EXCLUDED.sku, name = EXCLUDED.name, description = EXCLUDED.description, base_price = EXCLUDED.base_price",
                    p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getBasePrice()
                );
            });
        } else if ("Order".equals(syncLog.getEntityName())) {
            orderRepository.findById(syncLog.getEntityId()).ifPresent(o -> {
                cloudJdbcTemplate.update(
                    "INSERT INTO orders (id, customer_name, status, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET " +
                    "customer_name = EXCLUDED.customer_name, status = EXCLUDED.status, updated_at = EXCLUDED.updated_at",
                    o.getId(), o.getCustomerName(), o.getStatus().name(), o.getCreatedAt(), o.getUpdatedAt()
                );
            });
        } else if ("Warehouse".equals(syncLog.getEntityName())) {
            warehouseRepository.findById(syncLog.getEntityId()).ifPresent(w -> {
                cloudJdbcTemplate.update(
                    "INSERT INTO warehouses (id, name, location) " +
                    "VALUES (?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET " +
                    "name = EXCLUDED.name, location = EXCLUDED.location",
                    w.getId(), w.getName(), w.getLocation()
                );
            });
        } else if ("Stock".equals(syncLog.getEntityName())) {
            stockRepository.findById(syncLog.getEntityId()).ifPresent(s -> {
                cloudJdbcTemplate.update(
                    "INSERT INTO stocks (id, product_id, warehouse_id, quantity, last_updated, version) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET " +
                    "quantity = EXCLUDED.quantity, last_updated = EXCLUDED.last_updated, version = EXCLUDED.version",
                    s.getId(), s.getProductId(), s.getWarehouseId(), s.getQuantity(), s.getLastUpdated(), s.getVersion()
                );
            });
        }
    }
}
