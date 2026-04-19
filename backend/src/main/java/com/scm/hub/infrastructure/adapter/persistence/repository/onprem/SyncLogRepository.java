package com.scm.hub.infrastructure.adapter.persistence.repository.onprem;

import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing SyncLogEntity in the On-Premise database.
 * Used to track and query the status of synchronization tasks.
 */
@Repository
public interface SyncLogRepository extends JpaRepository<SyncLogEntity, UUID> {
    /**
     * Finds sync logs by their status.
     * @param status The status to filter by (e.g., PENDING, SUCCESS, FAILURE).
     * @return A list of sync log entities with the given status.
     */
    List<SyncLogEntity> findByStatus(String status);

    /**
     * Finds sync logs with statuses in the provided list.
     * @param statuses List of statuses to filter by.
     * @return A list of sync log entities with any of the given statuses.
     */
    List<SyncLogEntity> findByStatusIn(List<String> statuses);
}
