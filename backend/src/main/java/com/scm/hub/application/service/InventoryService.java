package com.scm.hub.application.service;

import com.scm.hub.domain.model.Product;
import com.scm.hub.domain.model.Stock;
import com.scm.hub.domain.model.Warehouse;
import com.scm.hub.domain.port.InventoryPort;
import com.scm.hub.infrastructure.adapter.rest.WebSocketController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service class for managing inventory operations.
 * Acts as an orchestrator between the API layer and the domain ports.
 * This service implements the business logic for managing products, warehouses, and their respective stock levels.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryPort inventoryPort;
    private final WebSocketController webSocketController;

    /**
     * Creates a new product in the system.
     * Behavior: Passes the product domain model to the inventory port for persistence.
     * @param product The product details to create. Must not be null.
     * @return The created product with its unique identifier.
     * @throws RuntimeException if SKU is duplicated.
     */
    public Product createProduct(Product product) {
        return inventoryPort.saveProduct(product);
    }

    /**
     * Retrieves all available products in the system.
     * @return A list of products. Returns an empty list if no products are found.
     */
    public List<Product> getAllProducts() {
        return inventoryPort.findAllProducts();
    }

    /**
     * Creates a new warehouse.
     * Behavior: Delegates warehouse creation to the persistence port.
     * @param warehouse The warehouse details. Must not be null.
     * @return The created warehouse.
     */
    public Warehouse createWarehouse(Warehouse warehouse) {
        return inventoryPort.saveWarehouse(warehouse);
    }

    /**
     * Retrieves all warehouses.
     * @return A list of warehouses. Returns an empty list if none exist.
     */
    public List<Warehouse> getAllWarehouses() {
        return inventoryPort.findAllWarehouses();
    }

    /**
     * Updates or creates a stock level for a specific product in a warehouse.
     * Behavior: Constructs a new Stock domain object and persists it via the port.
     * Constraints: 
     * <ul>
     *   <li>productId and warehouseId must refer to existing entities.</li>
     *   <li>quantity should be non-negative.</li>
     * </ul>
     * @param productId The ID of the product. Must not be null.
     * @param warehouseId The ID of the warehouse. Must not be null.
     * @param quantity The new stock quantity. Must be zero or positive.
     * @return The updated stock record.
     * @throws IllegalArgumentException if quantity is negative.
     */
    @org.springframework.transaction.annotation.Transactional
    public Stock updateStock(UUID productId, UUID warehouseId, Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        inventoryPort.findProductById(productId).orElseThrow(
                () -> new IllegalArgumentException("Product not found: " + productId));
        inventoryPort.findWarehouseById(warehouseId).orElseThrow(
                () -> new IllegalArgumentException("Warehouse not found: " + warehouseId));

        Stock stock = Stock.builder()
                .productId(productId)
                .warehouseId(warehouseId)
                .quantity(quantity)
                .lastUpdated(LocalDateTime.now())
                .build();
        Stock savedStock = inventoryPort.saveStock(stock);
        webSocketController.broadcastStockUpdate(savedStock);
        return savedStock;
    }

    /**
     * Retrieves the stock levels for all products in a specific warehouse.
     * @param warehouseId The ID of the warehouse. Must not be null.
     * @return A list of stock records. Returns an empty list if the warehouse has no stock or doesn't exist.
     */
    public List<Stock> getStockByWarehouse(UUID warehouseId) {
        return inventoryPort.findStockByWarehouse(warehouseId);
    }

    /**
     * Reduces stock quantity for a specific product in a warehouse.
     * Behavior: Retrieves current stock, reduces by the specified amount, and saves the update.
     * @param productId The ID of the product.
     * @param warehouseId The ID of the warehouse.
     * @param quantityToReduce The amount to reduce. Must be positive.
     * @return The updated stock record.
     * @throws IllegalArgumentException if insufficient stock or invalid parameters.
     */
    @org.springframework.transaction.annotation.Transactional
    public Stock reduceStock(UUID productId, UUID warehouseId, Integer quantityToReduce) {
        if (quantityToReduce <= 0) {
            throw new IllegalArgumentException("Quantity to reduce must be positive");
        }
        Stock currentStock = inventoryPort.findStockByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found for product and warehouse"));
        if (currentStock.getQuantity() < quantityToReduce) {
            throw new IllegalArgumentException("Insufficient stock: available " + currentStock.getQuantity() + ", requested " + quantityToReduce);
        }
        int newQuantity = currentStock.getQuantity() - quantityToReduce;
        return updateStock(productId, warehouseId, newQuantity);
    }

    /**
     * Searches inventory records based on product metadata and warehouse filters.
     * @param query Optional SKU or product name filter.
     * @param warehouseId Optional warehouse filter.
     * @param minQuantity Minimum stock quantity threshold.
     * @return Matching stock records.
     */
    public List<Stock> searchStock(String query, UUID warehouseId, Integer minQuantity) {
        return inventoryPort.searchStock(query, warehouseId, minQuantity);
    }
}
