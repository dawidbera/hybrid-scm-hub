package com.scm.hub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a synchronization log entry.
 * Tracks the status of data movement between on-premise and cloud environments for audit purposes.
 * <p>
 * Constraints:
 * <ul>
 *   <li>entityId must refer to a valid primary key of the specified entityName.</li>
 *   <li>Status typically transitions from PENDING to SUCCESS or FAILURE.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncLog {
    /** Unique identifier for the sync log entry */
    private UUID id;
    /** Name of the domain entity class being synchronized (e.g., "Order") */
    private String entityName;
    /** Unique identifier of the specific entity instance being tracked */
    private UUID entityId;
    /** Current status of the sync operation (e.g., "SUCCESS", "FAILURE") */
    private String status;
    /** Detailed error message if the synchronization failed */
    private String errorMessage;
    /** Timestamp when the synchronization event was logged */
    private LocalDateTime syncTimestamp;
}
