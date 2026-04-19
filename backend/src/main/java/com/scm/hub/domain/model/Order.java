package com.scm.hub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing an Order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private UUID id;
    private String customerName;
    private String status; // Created, Processing, Shipped
    private List<OrderItem> items;
    private Double total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}