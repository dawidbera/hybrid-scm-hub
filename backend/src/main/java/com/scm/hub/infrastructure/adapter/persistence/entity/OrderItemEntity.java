package com.scm.hub.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * JPA entity representing an order item in the database.
 * Maps the OrderItem domain model to the 'order_items' table.
 */
@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {
    /** Primary key for the order item */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Reference to the parent order entity */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    /** ID of the product ordered */
    private UUID productId;

    /** Number of units ordered */
    private Integer quantity;

    /** Unit price at the time of order */
    private Double price;
}
