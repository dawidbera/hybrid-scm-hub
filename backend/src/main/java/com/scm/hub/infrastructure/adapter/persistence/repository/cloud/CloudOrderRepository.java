package com.scm.hub.infrastructure.adapter.persistence.repository.cloud;

import com.scm.hub.infrastructure.adapter.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for order entities in the Cloud database.
 */
@Repository
public interface CloudOrderRepository extends JpaRepository<OrderEntity, UUID> {
}
