package com.scm.hub.application.service;

import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderItem;
import com.scm.hub.domain.port.OrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for order management.
 * Handles the lifecycle of orders, including creation, retrieval, and status updates.
 * Coordinates with the InventoryService to ensure stock is adjusted when orders are placed.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderPort orderPort;
    private final InventoryService inventoryService;

    /**
     * Creates a new order in the system.
     * Behavior:
     * 1. Calculates the total order price from items.
     * 2. Sets the initial status to "Created".
     * 3. Reduces stock for each item in the order via {@link InventoryService}.
     * 4. Persists the order via {@link OrderPort}.
     *
     * @param order The order details. Must not be null and should contain at least one item.
     * @return The created order with calculated total and timestamps.
     * @throws IllegalArgumentException if warehouse is not specified for an item or if stock is insufficient.
     */
    public Order createOrder(Order order) {
        // Calculate total from items, supporting null item lists.
        double total = order.getItems() == null ? 0.0 : order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotal(total);
        order.setStatus("Created");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Reduce stock for each item
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getWarehouseId() != null) {
                    inventoryService.reduceStock(item.getProductId(), item.getWarehouseId(), item.getQuantity());
                } else {
                    throw new IllegalArgumentException("Warehouse must be specified for order item: " + item.getProductId());
                }
            }
        }

        return orderPort.createOrder(order);
    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param id The UUID of the order.
     * @return The order if found, or null otherwise.
     */
    public Order getOrderById(UUID id) {
        return orderPort.getOrderById(id);
    }

    /**
     * Retrieves all orders in the system.
     *
     * @return A list of all orders.
     */
    public List<Order> getAllOrders() {
        return orderPort.getAllOrders();
    }

    /**
     * Updates the status of an existing order.
     *
     * @param id The UUID of the order to update.
     * @param status The new status (e.g., "Processing", "Shipped").
     * @return The updated order, or null if the order was not found.
     */
    public Order updateOrderStatus(UUID id, String status) {
        Order order = orderPort.getOrderById(id);
        if (order == null) {
            return null;
        }
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return orderPort.updateOrderStatus(id, status);
    }
}