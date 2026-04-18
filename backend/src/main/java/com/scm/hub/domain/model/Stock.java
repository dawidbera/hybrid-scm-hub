package com.scm.hub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing the Stock level of a specific product in a warehouse.
 * Bridges the relationship between Products and Warehouses with quantity information.
 * <p>
 * Constraints:
 * <ul>
 *   <li>Quantity cannot be negative.</li>
 *   <li>Each Product-Warehouse pair should ideally have only one Stock record.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    private UUID id;
    private UUID productId;
    private UUID warehouseId;
    private Integer quantity;
    private LocalDateTime lastUpdated;
}
