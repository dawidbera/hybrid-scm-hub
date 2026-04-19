package com.scm.hub.infrastructure.adapter.rest;

import com.scm.hub.application.service.InventoryService;
import com.scm.hub.domain.model.Product;
import com.scm.hub.domain.model.Stock;
import com.scm.hub.domain.model.Warehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for inventory management.
 * Provides endpoints for managing products, warehouses, and stock levels.
 * Exposes the application's capabilities to external clients via HTTP.
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Creates a new product.
     * @param product The product details in the request body.
     * @return A ResponseEntity containing the created product and HTTP 200 OK.
     * @throws RuntimeException if SKU is already taken.
     */
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(inventoryService.createProduct(product));
    }

    /**
     * Retrieves all products.
     * @return A list of all products in the system.
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }

    /**
     * Creates a new warehouse.
     * @param warehouse The warehouse details in the request body.
     * @return The created warehouse entity.
     */
    @PostMapping("/warehouses")
    public ResponseEntity<Warehouse> createWarehouse(@RequestBody Warehouse warehouse) {
        return ResponseEntity.ok(inventoryService.createWarehouse(warehouse));
    }

    /**
     * Retrieves all warehouses.
     * @return A list of all available warehouses.
     */
    @GetMapping("/warehouses")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        return ResponseEntity.ok(inventoryService.getAllWarehouses());
    }

    /**
     * Updates or creates stock for a product in a warehouse.
     * @param productId The UUID of the product.
     * @param warehouseId The UUID of the warehouse.
     * @param quantity The new quantity level.
     * @return The updated stock record.
     * @throws IllegalArgumentException if quantity is less than zero.
     */
    @PostMapping("/stock")
    public ResponseEntity<Stock> updateStock(@RequestParam UUID productId, 
                                            @RequestParam UUID warehouseId, 
                                            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateStock(productId, warehouseId, quantity));
    }

    /**
     * Advanced search for inventory across warehouses.
     * @param query Search query for product name or SKU
     * @param warehouseId Optional warehouse filter
     * @param minQuantity Minimum quantity filter
     * @return Filtered stock records
     */
    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchInventory(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID warehouseId,
            @RequestParam(required = false, defaultValue = "0") Integer minQuantity) {
        // This would require additional repository methods for advanced search
        // For now, return all stocks (placeholder implementation)
        return ResponseEntity.ok(inventoryService.getAllWarehouses().stream()
                .flatMap(w -> inventoryService.getStockByWarehouse(w.getId()).stream())
                .filter(s -> s.getQuantity() >= minQuantity)
                .toList());
    }
}
