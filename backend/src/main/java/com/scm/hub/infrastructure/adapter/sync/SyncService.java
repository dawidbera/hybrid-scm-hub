package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.ProductRepository;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @PersistenceContext(unitName = "cloud")
    private EntityManager cloudEntityManager;

    /**
     * Synchronizes a single entity based on the provided sync log.
     * The method fetches the entity from the On-Premise database and merges it into the Cloud database.
     * Updates the sync log status to SUCCESS or FAILURE based on the outcome.
     * 
     * @param syncLog The log entry containing details about the entity to synchronize.
     */
    @Transactional("cloudTransactionManager")
    public void syncEntity(SyncLogEntity syncLog) {
        try {
            if ("Product".equals(syncLog.getEntityName())) {
                productRepository.findById(syncLog.getEntityId()).ifPresent(product -> {
                    cloudEntityManager.merge(product);
                });
            }
            // Add other entities as needed
            
            syncLog.setStatus("SUCCESS");
            syncLog.setSyncTimestamp(LocalDateTime.now());
        } catch (Exception e) {
            syncLog.setStatus("FAILURE");
            syncLog.setErrorMessage(e.getMessage());
            syncLog.setSyncTimestamp(LocalDateTime.now());
            log.error("Failed to sync entity: {}", syncLog.getEntityId(), e);
        }
        syncLogRepository.save(syncLog);
    }
}
