package com.scm.hub.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing a Warehouse.
 * A warehouse is a physical location where stock is stored.
 * <p>
 * Constraints:
 * <ul>
 *   <li>Name should be descriptive and unique for identification.</li>
 *   <li>Location should provide enough detail for logistics routing.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    /** Unique identifier for the warehouse */
    private UUID id;
    /** Human-readable name of the warehouse facility */
    private String name;
    /** Geographical or physical location details of the warehouse */
    private String location;
}
