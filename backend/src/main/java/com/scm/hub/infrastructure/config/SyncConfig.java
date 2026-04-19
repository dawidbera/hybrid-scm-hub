package com.scm.hub.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for synchronization settings.
 */
@Component
@ConfigurationProperties(prefix = "sync")
@Data
public class SyncConfig {
    /**
     * The interval in milliseconds between synchronization runs.
     */
    private long intervalMs = 10000; // Default 10 seconds

    /**
     * Maximum number of retries for failed synchronization attempts.
     */
    private int maxRetries = 3;

    /**
     * Flag to enable Change Data Capture (CDC) based synchronization.
     */
    private boolean enableCdc = false; // Placeholder for CDC
}