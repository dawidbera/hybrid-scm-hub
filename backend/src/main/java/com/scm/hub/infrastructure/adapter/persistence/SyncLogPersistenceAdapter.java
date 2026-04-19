package com.scm.hub.infrastructure.adapter.persistence;

import com.scm.hub.domain.model.SyncLog;
import com.scm.hub.domain.port.SyncLogPort;
import com.scm.hub.infrastructure.adapter.persistence.mapper.SyncLogMapper;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Persistence adapter for synchronization log operations.
 * Implements {@link SyncLogPort} to provide access to the audit trail stored in the database.
 */
@Component
@RequiredArgsConstructor
public class SyncLogPersistenceAdapter implements SyncLogPort {

    private final SyncLogRepository syncLogRepository;
    private final SyncLogMapper syncLogMapper;

    /**
     * {@inheritDoc}
     * Behavior: Fetches all sync logs from the repository and maps them to domain models.
     */
    @Override
    public List<SyncLog> findAllSyncLogs() {
        return syncLogRepository.findAll().stream()
                .map(syncLogMapper::toDomain)
                .collect(Collectors.toList());
    }
}
