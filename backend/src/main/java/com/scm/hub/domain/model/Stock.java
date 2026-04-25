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
    /** Unique identifier for the stock record */
    private UUID id;
    /** ID of the product associated with this stock */
    private UUID productId;
    /** SKU of the product for display purposes */
    private String productSku;
    /** Name of the product for display purposes */
    private String productName;
    /** ID of the warehouse where this stock is physically located */
    private UUID warehouseId;
    /** Current available quantity of the product */
    private Integer quantity;
    /** Timestamp of the last stock level adjustment */
    private LocalDateTime lastUpdated;
}
