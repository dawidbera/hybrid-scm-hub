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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID productId;
    private UUID warehouseId;
    
    private Integer quantity;
    
    private LocalDateTime lastUpdated;

    @Version
    private Long version;
}
