package com.scm.hub.infrastructure.adapter.persistence.entity;

import com.scm.hub.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity used to track the status of data synchronization between environments.
 * Stores information about which entity was synced, when, and the outcome.
 */
@Entity
@Table(name = "sync_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncLogEntity {
    /** Primary key for the sync log */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Name of the entity type being synchronized */
    private String entityName;

    /** ID of the specific entity instance */
    private UUID entityId;

    /** Synchronization status */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /** Error message if synchronization failed */
    private String errorMessage;

    /** Timestamp when the sync operation was initiated or logged */
    private LocalDateTime syncTimestamp;

    /** Number of attempts made to synchronize this specific record */
    private Integer retryCount;
}
