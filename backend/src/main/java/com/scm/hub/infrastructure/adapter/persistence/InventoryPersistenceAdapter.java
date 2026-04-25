package com.scm.hub.infrastructure.adapter.persistence;

import com.scm.hub.domain.model.OrderStatus;
import com.scm.hub.domain.model.Product;
import com.scm.hub.domain.model.Stock;
import com.scm.hub.domain.model.Warehouse;
import com.scm.hub.domain.port.InventoryPort;
import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.StockEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.WarehouseEntity;
import com.scm.hub.infrastructure.adapter.persistence.mapper.InventoryMapper;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.StockRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Persistence adapter that implements the {@link InventoryPort}.
 * Connects the domain layer to the JPA repositories for the On-Premise database.
 * Handles the mapping between domain models and database entities.
 */
@Service
@RequiredArgsConstructor
public class InventoryPersistenceAdapter implements InventoryPort {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final SyncLogRepository syncLogRepository;
    private final InventoryMapper mapper;

    /**
     * Creates a new synchronization log entry to trigger background sync to the cloud.
     * @param entityName The name of the entity being synced.
     * @param entityId The ID of the entity instance.
     */
    private void createSyncLog(String entityName, UUID entityId) {
        SyncLogEntity syncLog = SyncLogEntity.builder()
                .entityName(entityName)
                .entityId(entityId)
                .status(OrderStatus.PENDING)
                .syncTimestamp(LocalDateTime.now())
                .build();
        syncLogRepository.save(syncLog);
    }

    /**
     * {@inheritDoc}
     * Behavior: Maps domain product to entity, saves it via JPA repository, and maps back to domain.
     */
    @Override
    @Transactional
    public Product saveProduct(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity saved = productRepository.save(entity);
        createSyncLog("Product", saved.getId());
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     * @param id Unique identifier of the product.
     * @return Optional containing the product if found, or empty otherwise.
     */
    @Override
    public Optional<Product> findProductById(UUID id) {
        return productRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     * @return List of all products in the on-premise database.
     */
    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Behavior: Converts domain warehouse to entity for persistent storage.
     */
    @Override
    @Transactional
    public Warehouse saveWarehouse(Warehouse warehouse) {
        WarehouseEntity entity = mapper.toEntity(warehouse);
        WarehouseEntity saved = warehouseRepository.save(entity);
        createSyncLog("Warehouse", saved.getId());
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     * @param id Unique identifier of the warehouse.
     * @return Optional containing the warehouse if found, or empty otherwise.
     */
    @Override
    public Optional<Warehouse> findWarehouseById(UUID id) {
        return warehouseRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     * @return List of all warehouses in the on-premise database.
     */
    @Override
    public List<Warehouse> findAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Enriches a Stock domain model with product metadata (SKU and Name).
     * @param stock The stock record to enrich.
     * @return The enriched stock record.
     */
    private Stock populateProductInfo(Stock stock) {
        if (stock == null) return null;
        productRepository.findById(stock.getProductId()).ifPresent(product -> {
            stock.setProductSku(product.getSku());
            stock.setProductName(product.getName());
        });
        return stock;
    }

    /**
     * {@inheritDoc}
     * Behavior: Saves stock levels. Uses transactional context to ensure data integrity.
     * Implements optimistic locking to handle concurrent updates.
     */
    @Override
    @Transactional
    public Stock saveStock(Stock stock) {
        if (stock.getQuantity() == null || stock.getQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity must be zero or positive.");
        }

        Optional<StockEntity> existingStock = stockRepository.findByProductIdAndWarehouseId(
                stock.getProductId(), stock.getWarehouseId());

        StockEntity entity;
        if (existingStock.isPresent()) {
            entity = existingStock.get();
            entity.setQuantity(stock.getQuantity());
            entity.setLastUpdated(stock.getLastUpdated());
        } else {
            entity = mapper.toEntity(stock);
        }

        try {
            StockEntity saved = stockRepository.save(entity);
            createSyncLog("Stock", saved.getId());
            return populateProductInfo(mapper.toDomain(saved));
        } catch (Exception e) {
            if (e.getCause() instanceof org.springframework.orm.ObjectOptimisticLockingFailureException) {
                throw new RuntimeException("Stock was modified by another transaction. Please retry.", e);
            }
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * @param warehouseId ID of the warehouse to filter by.
     * @return List of stock levels for the specified warehouse.
     */
    @Override
    public List<Stock> findStockByWarehouse(UUID warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId).stream()
                .map(mapper::toDomain)
                .map(this::populateProductInfo)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Performs a complex search across stock levels based on product properties and quantity.
     */
    @Override
    public List<Stock> searchStock(String query, UUID warehouseId, Integer minQuantity) {
        List<Stock> candidateStocks = stockRepository.findAll().stream()
                .map(mapper::toDomain)
                .map(this::populateProductInfo)
                .collect(Collectors.toList());

        if (warehouseId != null) {
            candidateStocks = candidateStocks.stream()
                    .filter(stock -> warehouseId.equals(stock.getWarehouseId()))
                    .collect(Collectors.toList());
        }

        if (query != null && !query.isBlank()) {
            var matchingProductIds = productRepository
                    .findBySkuContainingIgnoreCaseOrNameContainingIgnoreCase(query, query)
                    .stream()
                    .map(ProductEntity::getId)
                    .toList();
            candidateStocks = candidateStocks.stream()
                    .filter(stock -> matchingProductIds.contains(stock.getProductId()))
                    .collect(Collectors.toList());
        }

        if (minQuantity != null) {
            candidateStocks = candidateStocks.stream()
                    .filter(stock -> stock.getQuantity() != null && stock.getQuantity() >= minQuantity)
                    .collect(Collectors.toList());
        }

        return candidateStocks;
    }

    /**
     * {@inheritDoc}
     * @param productId ID of the product.
     * @return List of stock records for the product across all warehouses.
     */
    @Override
    public List<Stock> findStockByProduct(UUID productId) {
        return stockRepository.findByProductId(productId).stream()
                .map(mapper::toDomain)
                .map(this::populateProductInfo)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * @param productId ID of the product.
     * @param warehouseId ID of the warehouse.
     * @return Optional containing the specific stock record if it exists.
     */
    @Override
    public Optional<Stock> findStockByProductAndWarehouse(UUID productId, UUID warehouseId) {
        return stockRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .map(mapper::toDomain)
                .map(this::populateProductInfo);
    }
}
