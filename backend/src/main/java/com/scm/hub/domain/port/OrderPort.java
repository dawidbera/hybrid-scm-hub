package com.scm.hub.domain.port;

import com.scm.hub.domain.model.Order;

import java.util.List;
import java.util.UUID;

/**
 * Port interface for order operations.
 * Defines the contract for persistence and retrieval of orders, abstracting the underlying storage.
 */
public interface OrderPort {
    /**
     * Persists a new order or updates an existing one.
     * 
     * @param order The order to save.
     * @return The saved order.
     */
    Order createOrder(Order order);

    /**
     * Retrieves an order by its unique ID.
     * 
     * @param id The UUID of the order.
     * @return The order if found, or null otherwise.
     */
    Order getOrderById(UUID id);

    /**
     * Retrieves all orders from the storage.
     * 
     * @return A list of all orders.
     */
    List<Order> getAllOrders();

    /**
     * Updates the status of an order in the storage.
     * 
     * @param id The UUID of the order.
     * @param status The new status.
     * @return The updated order.
     */
    Order updateOrderStatus(UUID id, String status);
}