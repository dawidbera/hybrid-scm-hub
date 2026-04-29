package com.scm.hub.infrastructure.adapter.rest;

import com.scm.hub.application.service.OrderDocumentService;
import com.scm.hub.application.service.OrderService;
import com.scm.hub.domain.model.Order;
import com.scm.hub.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for order management.
 * Provides endpoints for creating, retrieving, and updating order statuses.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDocumentService orderDocumentService;

    /**
     * Creates a new order.
     * 
     * @param order The order data transfer object.
     * @return The created order.
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    /**
     * Retrieves an order by ID.
     * 
     * @param id The UUID of the order.
     * @return The order if found, or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        Order order = orderService.getOrderById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    /**
     * Retrieves all orders.
     * 
     * @return A list of all orders.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Updates the status of an existing order.
     * 
     * @param id The UUID of the order.
     * @param status The new status (CREATED, PROCESSING, SHIPPED).
     * @return The updated order, or 404 Not Found.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable UUID id, @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(id, status);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    /**
     * Downloads the order document from S3.
     *
     * @param id The UUID of the order.
     * @return The order document as a JSON file download.
     */
    @GetMapping("/{id}/document")
    public ResponseEntity<Resource> downloadOrderDocument(@PathVariable UUID id) {
        Resource resource = orderDocumentService.downloadOrderDocument(id);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"order-" + id + ".json\"")
                .body(resource);
    }
}
