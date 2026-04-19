package com.scm.hub.domain.port;

import com.scm.hub.domain.model.SyncLog;

import java.util.List;

/**
 * Port interface for synchronization log operations.
 * Defines the contract for accessing synchronization audit trails.
 */
public interface SyncLogPort {
    /**
     * Retrieves all synchronization logs.
     * 
     * @return A list of all synchronization logs.
     */
    List<SyncLog> findAllSyncLogs();
}
