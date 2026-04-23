package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.domain.model.OrderStatus;
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
     * Main sync method called by Spring Integration Service Activator.
     * Performs a single synchronization attempt. Retries are handled by the Integration flow.
     */
    public void syncEntity(SyncLogEntity syncLog) {
        try {
            log.info("Synchronizing {} (ID: {})", syncLog.getEntityName(), syncLog.getEntityId());
            cloudSyncProcessor.pushToCloud(syncLog);
            updateSyncLogStatus(syncLog, true, 0, null);
        } catch (Exception e) {
            log.error("Sync failed for {} (ID: {}): {}", 
                    syncLog.getEntityName(), syncLog.getEntityId(), e.getMessage());
            updateSyncLogStatus(syncLog, false, 0, e.getMessage());
            throw e; // Rethrow to trigger Spring Integration retry
        }
    }

    /**
     * Updates the synchronization log status in the On-Premise database.
     */
    @Transactional(value = "onPremTransactionManager")
    public void updateSyncLogStatus(SyncLogEntity syncLog, boolean success, int retryCount, String error) {
        syncLog.setStatus(success ? OrderStatus.SUCCESS : OrderStatus.FAILURE);
        syncLog.setSyncTimestamp(LocalDateTime.now());
        syncLog.setErrorMessage(error);
        // We could increment retry count here if we wanted to track it specifically
        syncLogRepository.save(syncLog);
        
        if (success) {
            webSocketController.broadcastAuditLog(syncLog);
        }
    }
}
