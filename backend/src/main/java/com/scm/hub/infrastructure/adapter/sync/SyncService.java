package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.OrderRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import com.scm.hub.infrastructure.adapter.rest.WebSocketController;
import com.scm.hub.infrastructure.config.SyncConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for synchronizing data between the On-Premise and Cloud databases.
 * It handles the actual transfer of entity state for specific records marked for sync.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SyncService {

    private final ProductRepository productRepository;
    private final SyncLogRepository syncLogRepository;
    private final OrderRepository orderRepository;
    private final WebSocketController webSocketController;
    private final SyncConfig syncConfig;

    @PersistenceContext(unitName = "cloud")
    private EntityManager cloudEntityManager;

    /**
     * Synchronizes a single entity based on the provided sync log.
     * The method fetches the entity from the On-Premise database and merges it into the Cloud database.
     * Updates the sync log status to SUCCESS or FAILURE based on the outcome.
     * Includes retry logic for failed synchronizations.
     * 
     * @param syncLog The log entry containing details about the entity to synchronize.
     */
    @Transactional("cloudTransactionManager")
    public void syncEntity(SyncLogEntity syncLog) {
        int maxRetries = syncConfig.getMaxRetries();
        int attempt = 0;
        boolean success = false;

        while (attempt < maxRetries && !success) {
            attempt++;
            try {
                if ("Product".equals(syncLog.getEntityName())) {
                    productRepository.findById(syncLog.getEntityId()).ifPresent(product -> {
                        cloudEntityManager.merge(product);
                    });
                } else if ("Order".equals(syncLog.getEntityName())) {
                    orderRepository.findById(syncLog.getEntityId()).ifPresent(order -> {
                        cloudEntityManager.merge(order);
                    });
                } else if ("Warehouse".equals(syncLog.getEntityName())) {
                    // Add Warehouse sync if needed
                } else if ("Stock".equals(syncLog.getEntityName())) {
                    // Add Stock sync if needed
                }
                // Add other entities as needed
                cloudEntityManager.flush();
                syncLog.setStatus("SUCCESS");
                syncLog.setSyncTimestamp(LocalDateTime.now());
                syncLog.setRetryCount(attempt - 1);
                success = true;
            } catch (Exception e) {
                if (attempt >= maxRetries) {
                    syncLog.setStatus("FAILURE");
                    syncLog.setErrorMessage(e.getMessage());
                    syncLog.setSyncTimestamp(LocalDateTime.now());
                    syncLog.setRetryCount(attempt - 1);
                    log.error("Failed to sync entity after {} attempts: {}", maxRetries, syncLog.getEntityId(), e);
                } else {
                    log.warn("Sync attempt {} failed for entity {}: {}", attempt, syncLog.getEntityId(), e.getMessage());
                    try {
                        Thread.sleep(1000 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        syncLogRepository.save(syncLog);
        if (success) {
            webSocketController.broadcastAuditLog(syncLog);
        }
    }
}
