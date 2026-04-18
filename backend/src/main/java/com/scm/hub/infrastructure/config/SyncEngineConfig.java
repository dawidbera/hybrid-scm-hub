package com.scm.hub.infrastructure.config;

import com.scm.hub.infrastructure.adapter.persistence.entity.SyncLogEntity;
import com.scm.hub.infrastructure.adapter.persistence.repository.onprem.SyncLogRepository;
import com.scm.hub.infrastructure.adapter.sync.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.List;

/**
 * Configuration for the Synchronization Engine using Spring Integration.
 * This component is responsible for orchestrating the data flow between
 * the On-Premise database and the Cloud simulation.
 */
@Configuration
@RequiredArgsConstructor
public class SyncEngineConfig {

    private final SyncLogRepository syncLogRepository;
    private final SyncService syncService;

    /**
     * Defines the channel used for carrying synchronization messages.
     * @return The message channel for sync tasks.
     */
    @Bean
    public MessageChannel syncChannel() {
        return new DirectChannel();
    }

    /**
     * Inbound channel adapter that polls for pending synchronization logs.
     * Fetches all SyncLogEntity records with a 'PENDING' status from the On-Premise repository.
     * The results are wrapped in a Spring Messaging Message and sent to the 'syncChannel'.
     * 
     * @return A message source providing lists of sync logs for processing.
     */
    @Bean
    @InboundChannelAdapter(value = "syncChannel", poller = @Poller(fixedDelay = "5000"))
    public MessageSource<List<SyncLogEntity>> syncLogSource() {
        return () -> {
            List<SyncLogEntity> pendingLogs = syncLogRepository.findAll().stream()
                    .filter(log -> "PENDING".equals(log.getStatus()))
                    .toList();
            return pendingLogs.isEmpty() ? null : new GenericMessage<>(pendingLogs);
        };
    }
}
