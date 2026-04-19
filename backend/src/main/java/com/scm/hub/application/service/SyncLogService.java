package com.scm.hub.application.service;

import com.scm.hub.domain.model.SyncLog;
import com.scm.hub.domain.port.SyncLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for reading synchronization audit logs.
 * Provides access to the history of data synchronization between the on-premise and cloud environments.
 */
@Service
@RequiredArgsConstructor
public class SyncLogService {

    private final SyncLogPort syncLogPort;

    /**
     * Retrieves all synchronization audit logs.
     * Behavior: Fetches the logs from the persistence layer via the SyncLogPort.
     * 
     * @return A list of all {@link SyncLog} entries.
     */
    public List<SyncLog> getAuditLogs() {
        return syncLogPort.findAllSyncLogs();
    }
}
