package com.scm.hub.infrastructure.adapter.rest;

import com.scm.hub.domain.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for broadcasting real-time updates.
 * Used to push notifications about stock changes and audit logs to connected clients.
 */
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcasts a stock update to all subscribers of the /topic/stock-updates destination.
     * 
     * @param stock The updated stock domain model.
     */
    public void broadcastStockUpdate(Stock stock) {
        messagingTemplate.convertAndSend("/topic/stock-updates", stock);
    }

    /**
     * Broadcasts an audit log entry to all subscribers of the /topic/audit-logs destination.
     * 
     * @param auditLog The audit log object to broadcast.
     */
    public void broadcastAuditLog(Object auditLog) {
        messagingTemplate.convertAndSend("/topic/audit-logs", auditLog);
    }
}