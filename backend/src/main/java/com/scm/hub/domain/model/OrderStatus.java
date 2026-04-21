package com.scm.hub.domain.model;

/**
 * Enumeration of possible statuses used throughout the application.
 * Covers both order lifecycle and synchronization processes.
 */
public enum OrderStatus {
    // Order specific statuses
    /** Order has been created but not yet processed */
    CREATED,
    /** Order is currently being prepared or processed */
    PROCESSING,
    /** Order has been shipped to the customer */
    SHIPPED,

    // Synchronization specific statuses
    /** Synchronization is waiting to be processed */
    PENDING,
    /** Synchronization completed successfully */
    SUCCESS,
    /** Synchronization failed */
    FAILURE
}
