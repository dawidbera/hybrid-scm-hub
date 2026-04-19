package com.scm.hub.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing the stock level in the database.
 * Maps the Stock domain model to the 'stocks' table and includes versioning for optimistic locking.
 * <p>
 * Constraints and Behavior:
 * <ul>
 *   <li>The 'version' field enables optimistic concurrency control to prevent lost updates in a multi-user environment.</li>
 *   <li>'productId' and 'warehouseId' link the stock to its respective entities.</li>
 * </ul>
 */
@Entity
@Table(name = "stocks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEntity {
    /** Primary key for the stock record */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** ID of the product this stock belongs to */
    private UUID productId;

    /** ID of the warehouse where the stock is stored */
    private UUID warehouseId;
    
    /** Current quantity available */
    private Integer quantity;
    
    /** Timestamp of the last quantity update */
    private LocalDateTime lastUpdated;

    /** Version field for JPA optimistic locking */
    @Version
    private Long version;
}
