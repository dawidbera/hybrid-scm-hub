package com.scm.hub.infrastructure.config;

import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.sync.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.messaging.MessageChannel;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.retry.support.RetryTemplate;

/**
 * Configuration for Spring Integration based synchronization engine.
 * Replaces the simple @Scheduled poller with a robust message-driven architecture.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sync.enabled", havingValue = "true", matchIfMissing = true)
public class IntegrationConfig {

    private final DataSource onPremDataSource;
    private final SyncService syncService;
    private final SyncConfig syncConfig;

    /**
     * Channel for transporting sync log entries.
     */
    @Bean
    public MessageChannel syncChannel() {
        return new DirectChannel();
    }

    /**
     * Inbound adapter that polls the On-Premise database for pending sync logs.
     */
    @Bean
    @InboundChannelAdapter(value = "syncChannel", poller = @Poller(fixedDelay = "${sync.interval-ms:10000}"))
    public MessageSource<Object> jdbcInboundAdapter() {
        JdbcPollingChannelAdapter adapter = new JdbcPollingChannelAdapter(onPremDataSource,
                "SELECT id, entity_name, entity_id, status FROM sync_logs WHERE status IN ('PENDING', 'FAILURE') ORDER BY sync_timestamp ASC");
        adapter.setRowMapper((rs, rowNum) -> {
            SyncLogEntity entity = new SyncLogEntity();
            entity.setId(java.util.UUID.fromString(rs.getString("id")));
            entity.setEntityName(rs.getString("entity_name"));
            entity.setEntityId(java.util.UUID.fromString(rs.getString("entity_id")));
            entity.setStatus(com.scm.hub.domain.model.OrderStatus.valueOf(rs.getString("status")));
            return entity;
        });
        adapter.setMaxRowsPerPoll(10);
        adapter.setUpdateSql("UPDATE sync_logs SET status = 'PROCESSING' WHERE id = :id");
        return adapter;
    }

    /**
     * Retry advice to handle transient failures during synchronization.
     */
    @Bean
    public RequestHandlerRetryAdvice retryAdvice() {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(syncConfig.getMaxRetries())
                .exponentialBackoff(1000, 2, 10000)
                .build();
        advice.setRetryTemplate(retryTemplate);
        return advice;
    }

    /**
     * Service activator that consumes messages from the sync channel and triggers the sync process.
     * Includes retry logic via advice chain.
     */
    @ServiceActivator(inputChannel = "syncChannel", adviceChain = "retryAdvice")
    public void handleSync(SyncLogEntity syncLog) {
        syncService.syncEntity(syncLog);
    }
}
