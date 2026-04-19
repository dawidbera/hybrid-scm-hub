package com.scm.hub.infrastructure.adapter.persistence.entity;

import com.scm.hub.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity representing an order in the database.
 * Maps the Order domain model to the 'orders' table.
 */
@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    /** Primary key for the order */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Name of the customer who placed the order */
    private String customerName;

    /** Lifecycle status of the order (CREATED, PROCESSING, SHIPPED) */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /** Collection of items belonging to this order, handled with lazy loading */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> items;

    /** Timestamp of record creation */
    private LocalDateTime createdAt;

    /** Timestamp of the last record update */
    private LocalDateTime updatedAt;
}
