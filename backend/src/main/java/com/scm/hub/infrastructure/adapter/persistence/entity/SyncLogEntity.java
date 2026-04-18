package com.scm.hub.infrastructure.adapter.persistence.entity;

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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String entityName;
    private UUID entityId;
    private String status; // PENDING, SUCCESS, FAILURE
    private String errorMessage;
    private LocalDateTime syncTimestamp;
}
