package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Component responsible for periodically triggering the data synchronization process.
 * It polls the On-Premise database for pending sync logs and initiates synchronization.
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SyncScheduler {

    private final SyncLogRepository syncLogRepository;
    private final SyncService syncService;

    /**
     * Periodically executes the synchronization task.
     * Finds all sync logs with a 'PENDING' status and delegates their synchronization to the SyncService.
     * Runs with a fixed delay of 10 seconds between the end of the last execution and the start of the next.
     */
    @Scheduled(fixedDelay = 10000)
    public void runSync() {
        log.info("Starting synchronization process...");
        syncLogRepository.findAll().stream()
                .filter(log -> "PENDING".equals(log.getStatus()))
                .forEach(syncService::syncEntity);
        log.info("Synchronization process finished.");
    }
}
