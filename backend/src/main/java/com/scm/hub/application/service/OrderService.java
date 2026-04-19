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
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderPort orderPort;

    public Order createOrder(Order order) {
        // Calculate total from items
        double total = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotal(total);
        order.setStatus("Created");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderPort.createOrder(order);
    }

    public Order getOrderById(UUID id) {
        return orderPort.getOrderById(id);
    }

    public List<Order> getAllOrders() {
        return orderPort.getAllOrders();
    }

    public Order updateOrderStatus(UUID id, String status) {
        Order order = orderPort.getOrderById(id);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return orderPort.updateOrderStatus(id, status);
    }
}