package com.scm.hub.domain.model;

/**
 * Enumeration of possible order lifecycle statuses.
 */
public enum OrderStatus {
    /** Order has been created but not yet processed */
    CREATED,
    /** Order is currently being prepared or processed */
    PROCESSING,
    /** Order has been shipped to the customer */
    SHIPPED
}
