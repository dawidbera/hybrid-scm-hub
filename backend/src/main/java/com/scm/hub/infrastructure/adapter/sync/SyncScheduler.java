package com.scm.hub.infrastructure.adapter.sync;

import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import com.scm.hub.infrastructure.config.SyncConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import java.util.List;

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
    private final SyncConfig syncConfig;

    /**
     * Periodically executes the synchronization task.
     * Finds all sync logs with a 'PENDING' or 'FAILURE' status and delegates their synchronization to the SyncService.
     * Runs with a fixed delay of 10 seconds between the end of the last execution and the start of the next.
     */
    @Scheduled(fixedDelay = 10000)
    public void runSync() {
        log.info("Starting synchronization process...");
        syncLogRepository.findByStatusIn(List.of("PENDING", "FAILURE")).forEach(syncService::syncEntity);
        log.info("Synchronization process finished.");
    }
}
