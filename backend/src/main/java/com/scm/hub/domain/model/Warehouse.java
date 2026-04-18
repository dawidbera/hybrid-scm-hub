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
    private UUID id;
    private String name;
    private String location;
}
