package com.scm.hub.infrastructure.adapter.persistence.repository.onprem;

import com.scm.hub.infrastructure.adapter.persistence.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Repository interface for managing StockEntity in the On-Premise database.
 * Provides custom query methods for filtering stock by warehouse or product.
 */
@Repository
public interface StockRepository extends JpaRepository<StockEntity, UUID> {
    /**
     * Finds all stock records associated with a specific warehouse.
     * @param warehouseId The unique identifier of the warehouse.
     * @return A list of stock entities.
     */
    List<StockEntity> findByWarehouseId(UUID warehouseId);

    /**
     * Finds all stock records associated with a specific product.
     * @param productId The unique identifier of the product.
     * @return A list of stock entities.
     */
    List<StockEntity> findByProductId(UUID productId);

    /**
     * Finds a stock record for a specific product in a specific warehouse.
     * @param productId The product UUID.
     * @param warehouseId The warehouse UUID.
     * @return An optional stock entity.
     */
    Optional<StockEntity> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);
}
