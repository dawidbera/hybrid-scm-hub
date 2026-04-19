package com.scm.hub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing an individual item within an Order.
 * Links a product from a specific warehouse to an order with quantity and price details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    /** Unique identifier for the order item */
    private UUID id;
    /** ID of the parent order */
    private UUID orderId;
    /** ID of the product being ordered */
    private UUID productId;
    /** ID of the warehouse from which the stock will be fulfilled */
    private UUID warehouseId;
    /** Number of units of the product ordered */
    private Integer quantity;
    /** Unit price of the product at the time of the order */
    private Double price;
}
