package com.scm.hub.infrastructure.adapter.persistence.repository.onprem;

import com.scm.hub.infrastructure.adapter.persistence.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing WarehouseEntity in the On-Premise database.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {
}
