package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import com.scm.hub.infrastructure.adapter.rest.WebSocketController;
import com.scm.hub.infrastructure.config.SyncConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for synchronizing data between the On-Premise and Cloud databases.
 * Orchestrates the overall sync process, including retries and status reporting.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SyncService {

    private final SyncLogRepository syncLogRepository;
    private final CloudSyncProcessor cloudSyncProcessor;
    private final WebSocketController webSocketController;
    private final SyncConfig syncConfig;

    /**
     * Main sync method. Orchestrates the process and updates the log in On-Premise DB.
     * Note: This method is not @Transactional to avoid holding an On-Prem transaction 
     * while performing potentially slow network/Cloud operations.
     */
    public void syncEntity(SyncLogEntity syncLog) {
        int maxRetries = syncConfig.getMaxRetries();
        int attempt = 0;
        boolean success = false;
        String lastError = null;

        while (attempt < maxRetries && !success) {
            attempt++;
            try {
                // Delegation to a transactional component ensures the Cloud transaction is correctly managed.
                cloudSyncProcessor.pushToCloud(syncLog);
                success = true;
            } catch (Exception e) {
                lastError = e.getMessage();
                log.warn("Sync attempt {} failed for entity {} (ID: {}): {}", 
                        attempt, syncLog.getEntityName(), syncLog.getEntityId(), lastError);
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        updateSyncLogStatus(syncLog, success, attempt, lastError);
    }

    /**
     * Updates the synchronization log status in the On-Premise database.
     * This method runs in its own transaction on the primary (on-prem) transaction manager.
     */
    @Transactional(value = "onPremTransactionManager")
    public void updateSyncLogStatus(SyncLogEntity syncLog, boolean success, int attempt, String error) {
        syncLog.setStatus(success ? "SUCCESS" : "FAILURE");
        syncLog.setSyncTimestamp(LocalDateTime.now());
        syncLog.setRetryCount(attempt - 1);
        syncLog.setErrorMessage(error);
        syncLogRepository.save(syncLog);
        
        if (success) {
            webSocketController.broadcastAuditLog(syncLog);
        }
    }
}
