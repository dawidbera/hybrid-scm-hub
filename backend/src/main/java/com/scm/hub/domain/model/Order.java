package com.scm.hub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing an Order in the SCM system.
 * Encapsulates order identity, customer information, status, and the list of ordered items.
 * <p>
 * Constraints:
 * <ul>
 *   <li>Status follows the lifecycle: Created -> Processing -> Shipped.</li>
 *   <li>Total is calculated based on the sum of individual item prices and quantities.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    /** Unique identifier for the order */
    private UUID id;
    /** Name of the customer who placed the order */
    private String customerName;
    /** Current status in the order lifecycle (e.g., CREATED, PROCESSING, SHIPPED) */
    private OrderStatus status;
    /** List of individual items included in this order */
    private List<OrderItem> items;
    /** Total calculated monetary value of the order */
    private Double total;
    /** Timestamp when the order was initially created */
    private LocalDateTime createdAt;
    /** Timestamp of the last update to the order state */
    private LocalDateTime updatedAt;
}
