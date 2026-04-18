package com.scm.hub.infrastructure.adapter.persistence.mapper;

import com.scm.hub.domain.model.Product;
import com.scm.hub.domain.model.Stock;
import com.scm.hub.domain.model.Warehouse;
import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.StockEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.WarehouseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper component for converting between Domain Models and JPA Entities.
 * Facilitates the separation of concerns by ensuring the domain layer remains decoupled from persistence details.
 */
@Component
public class InventoryMapper {

    /**
     * Converts a ProductEntity to its Domain Model representation.
     * @param entity The database entity.
     * @return The domain model, or null if the entity is null.
     */
    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        return Product.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .name(entity.getName())
                .description(entity.getDescription())
                .basePrice(entity.getBasePrice())
                .build();
    }

    /**
     * Converts a Product domain model to its Entity representation.
     * @param product The domain model.
     * @return The database entity, or null if the product is null.
     */
    public ProductEntity toEntity(Product product) {
        if (product == null) return null;
        return ProductEntity.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .build();
    }

    /**
     * Converts a WarehouseEntity to its Domain Model.
     * @param entity The database entity.
     * @return The domain model, or null if input is null.
     */
    public Warehouse toDomain(WarehouseEntity entity) {
        if (entity == null) return null;
        return Warehouse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .location(entity.getLocation())
                .build();
    }

    /**
     * Converts a Warehouse domain model to its Entity representation.
     * @param warehouse The domain model.
     * @return The database entity, or null if input is null.
     */
    public WarehouseEntity toEntity(Warehouse warehouse) {
        if (warehouse == null) return null;
        return WarehouseEntity.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .build();
    }

    /**
     * Converts a StockEntity to its Domain Model representation.
     * @param entity The database entity.
     * @return The domain model, or null if input is null.
     */
    public Stock toDomain(StockEntity entity) {
        if (entity == null) return null;
        return Stock.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .warehouseId(entity.getWarehouseId())
                .quantity(entity.getQuantity())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }

    /**
     * Converts a Stock domain model to its Entity representation.
     * @param stock The domain model.
     * @return The database entity, or null if input is null.
     */
    public StockEntity toEntity(Stock stock) {
        if (stock == null) return null;
        return StockEntity.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .warehouseId(stock.getWarehouseId())
                .quantity(stock.getQuantity())
                .lastUpdated(stock.getLastUpdated())
                .build();
    }
}
