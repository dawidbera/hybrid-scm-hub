package com.scm.hub.infrastructure.adapter.persistence.repository.onprem;

import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing SyncLogEntity in the On-Premise database.
 * Used to track and query the status of synchronization tasks.
 */
@Repository
public interface SyncLogRepository extends JpaRepository<SyncLogEntity, UUID> {
}
