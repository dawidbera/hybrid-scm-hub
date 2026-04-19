package com.scm.hub.domain.port;

import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderStatus;

import java.util.List;
import java.util.UUID;

/**
 * Output port for Order persistence operations.
 * Defines the contract for storing and retrieving order data.
 */
public interface OrderPort {
    /**
     * Saves a new order to the storage.
     * 
     * @param order The order to save.
     * @return The saved order.
     */
    Order saveOrder(Order order);

    /**
     * Retrieves an order by its unique identifier.
     * 
     * @param id The order identifier.
     * @return The order, if found.
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
     * @param id The order identifier.
     * @param status The new status.
     * @return The updated order.
     */
    Order updateOrderStatus(UUID id, OrderStatus status);
}
