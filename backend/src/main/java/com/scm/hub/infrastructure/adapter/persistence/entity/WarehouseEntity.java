package com.scm.hub.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * JPA entity representing a warehouse in the database.
 * Maps the Warehouse domain model to the 'warehouses' table.
 */
@Entity
@Table(name = "warehouses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseEntity {
    /** Primary key for the warehouse */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Descriptive name of the warehouse */
    private String name;

    /** Physical or geographical location details */
    private String location;
}
