package com.scm.hub.infrastructure.adapter.persistence.repository.onprem;

import com.scm.hub.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing ProductEntity in the On-Premise database.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    /**
     * Searches for products where either the SKU or the name contains the given query strings, ignoring case.
     * @param sku The SKU search string.
     * @param name The name search string.
     * @return A list of matching product entities.
     */
    List<ProductEntity> findBySkuContainingIgnoreCaseOrNameContainingIgnoreCase(String sku, String name);
}
