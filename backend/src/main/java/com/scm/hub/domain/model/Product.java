package com.scm.hub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain model representing a Product in the SCM system.
 * Contains information about the product's identity, SKU, and pricing.
 * <p>
 * Constraints:
 * <ul>
 *   <li>SKU should be unique across the system.</li>
 *   <li>Base price must be non-negative.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal basePrice;
}
