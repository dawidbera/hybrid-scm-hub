package com.scm.hub.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA entity representing a product in the database.
 * Maps the Product domain model to the 'products' table.
 * <p>
 * Constraints:
 * <ul>
 *   <li>The 'sku' field is unique and mandatory, acting as a natural business key.</li>
 *   <li>The 'id' is a primary key generated automatically.</li>
 * </ul>
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    /** Primary key for the product */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Unique Stock Keeping Unit identifier */
    @Column(unique = true, nullable = false)
    private String sku;

    /** Display name of the product */
    private String name;

    /** Textual description of the product */
    private String description;

    /** Default selling price */
    private BigDecimal basePrice;
}
