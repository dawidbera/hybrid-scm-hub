package com.scm.hub.infrastructure.adapter.persistence.repository.onprem;

import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for managing ProductEntity in the On-Premise database.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
