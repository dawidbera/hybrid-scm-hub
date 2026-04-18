package com.scm.hub.infrastructure.adapter.persistence;

import com.scm.hub.domain.model.Product;
import com.scm.hub.domain.model.Stock;
import com.scm.hub.domain.model.Warehouse;
import com.scm.hub.domain.port.InventoryPort;
import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.StockEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.WarehouseEntity;
import com.scm.hub.infrastructure.adapter.persistence.mapper.InventoryMapper;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.StockRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final InventoryMapper mapper;

    /**
     * {@inheritDoc}
     * Behavior: Maps domain product to entity, saves it via JPA repository, and maps back to domain.
     */
    @Override
    @Transactional
    public Product saveProduct(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity saved = productRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findProductById(UUID id) {
        return productRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
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
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Warehouse> findWarehouseById(UUID id) {
        return warehouseRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Warehouse> findAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Behavior: Saves stock levels. Uses transactional context to ensure data integrity.
     */
    @Override
    @Transactional
    public Stock saveStock(Stock stock) {
        StockEntity entity = mapper.toEntity(stock);
        StockEntity saved = stockRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Stock> findStockByWarehouse(UUID warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Stock> findStockByProduct(UUID productId) {
        return stockRepository.findByProductId(productId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
