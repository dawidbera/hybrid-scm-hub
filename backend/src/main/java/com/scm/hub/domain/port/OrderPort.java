package com.scm.hub.domain.port;

import com.scm.hub.domain.model.Order;

import java.util.List;
import java.util.UUID;

/**
 * Port interface for order operations.
 */
public interface OrderPort {
    Order createOrder(Order order);
    Order getOrderById(UUID id);
    List<Order> getAllOrders();
    Order updateOrderStatus(UUID id, String status);
}