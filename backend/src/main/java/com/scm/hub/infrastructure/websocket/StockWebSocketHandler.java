package com.scm.hub.infrastructure.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for broadcasting stock updates to connected clients.
 * Manages active WebSocket sessions and provides a broadcast mechanism.
 */
public class StockWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Invoked after a new WebSocket connection is established.
     * Adds the session to the set of active sessions.
     * 
     * @param session The newly opened WebSocket session.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    /**
     * Invoked after a WebSocket connection is closed.
     * Removes the session from the set of active sessions.
     * 
     * @param session The closed WebSocket session.
     * @param status The close status.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    /**
     * Broadcasts a text message to all currently open WebSocket sessions.
     * 
     * @param message The message to broadcast.
     */
    public void broadcast(String message) {
        TextMessage textMessage = new TextMessage(message);
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (Exception ignored) {
                }
            }
        });
    }
}
