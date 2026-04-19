package com.scm.hub.infrastructure.adapter.persistence.mapper;

import com.scm.hub.domain.model.SyncLog;
import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for SyncLog entities and domain models.
 * Facilitates conversion of {@link SyncLogEntity} to the {@link SyncLog} domain model.
 */
@Component
public class SyncLogMapper {

    /**
     * Converts a SyncLogEntity to its domain model representation.
     * 
     * @param entity The JPA entity to convert.
     * @return The corresponding domain model.
     */
    public SyncLog toDomain(SyncLogEntity entity) {
        if (entity == null) {
            return null;
        }
        return SyncLog.builder()
                .id(entity.getId())
                .entityName(entity.getEntityName())
                .entityId(entity.getEntityId())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .syncTimestamp(entity.getSyncTimestamp())
                .build();
    }
}
