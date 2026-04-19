package com.scm.hub.domain.port;

import com.scm.hub.domain.model.Product;
import com.scm.hub.domain.model.Warehouse;
import com.scm.hub.domain.model.Stock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for inventory-related persistence operations.
 * Follows the Hexagonal Architecture pattern to decouple domain logic from infrastructure.
 * Acts as an abstraction for data access, allowing the domain to remain independent of the underlying storage technology.
 */
public interface InventoryPort {
    /**
     * Persists a product domain model.
     * Behavior: Saves the product to the underlying storage. If the product has an ID that exists, it updates the record.
     * @param product The product domain model to save. Must not be null.
     * @return The saved product with its assigned ID if it was new.
     * @throws RuntimeException if the SKU is a duplicate or if persistence fails.
     */
    Product saveProduct(Product product);

    /**
     * Finds a product by its unique identifier.
     * @param id The product UUID. Must not be null.
     * @return An Optional containing the product if found, or an empty Optional if no product exists with the given ID.
     */
    Optional<Product> findProductById(UUID id);

    /**
     * Retrieves all products in the system.
     * @return A list of all products, or an empty list if none exist.
     */
    List<Product> findAllProducts();

    /**
     * Persists a warehouse domain model.
     * Behavior: Saves the warehouse to the storage. Performs an update if the warehouse already exists.
     * @param warehouse The warehouse domain model to save. Must not be null.
     * @return The saved warehouse.
     */
    Warehouse saveWarehouse(Warehouse warehouse);

    /**
     * Finds a warehouse by its unique identifier.
     * @param id The warehouse UUID. Must not be null.
     * @return An Optional containing the warehouse if found, or empty if not found.
     */
    Optional<Warehouse> findWarehouseById(UUID id);

    /**
     * Retrieves all warehouses in the system.
     * @return A list of all warehouses, or an empty list if none exist.
     */
    List<Warehouse> findAllWarehouses();

    /**
     * Persists a stock level.
     * Behavior: Updates or creates a stock record for a product-warehouse combination.
     * Constraints: The quantity should be non-negative.
     * @param stock The stock domain model to save. Must not be null.
     * @return The saved stock.
     * @throws RuntimeException if the referenced product or warehouse does not exist.
     */
    Stock saveStock(Stock stock);

    /**
     * Finds all stock records for a specific warehouse.
     * @param warehouseId The warehouse UUID. Must not be null.
     * @return A list of stock records for the warehouse, or empty list if the warehouse has no stock.
     */
    List<Stock> findStockByWarehouse(UUID warehouseId);

    /**
     * Searches stock records by product query and optional warehouse filters.
     * @param query Optional product SKU or name to search.
     * @param warehouseId Optional warehouse filter.
     * @param minQuantity Minimum quantity threshold.
     * @return A filtered list of stock records.
     */
    List<Stock> searchStock(String query, UUID warehouseId, Integer minQuantity);

    /**
     * Finds all stock records for a specific product across all warehouses.
     * @param productId The product UUID. Must not be null.
     * @return A list of stock records for the product, or empty list if the product is not in any warehouse.
     */
    List<Stock> findStockByProduct(UUID productId);

    /**
     * Finds a specific stock record for a product in a warehouse.
     * @param productId The product UUID. Must not be null.
     * @param warehouseId The warehouse UUID. Must not be null.
     * @return An Optional containing the stock if found, or empty if not found.
     */
    Optional<Stock> findStockByProductAndWarehouse(UUID productId, UUID warehouseId);
}
