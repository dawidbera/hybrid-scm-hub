package com.scm.hub.infrastructure.adapter.rest;

import com.scm.hub.domain.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for broadcasting real-time updates.
 */
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastStockUpdate(Stock stock) {
        messagingTemplate.convertAndSend("/topic/stock-updates", stock);
    }
}