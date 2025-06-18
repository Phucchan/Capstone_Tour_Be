package com.fpt.capstone.tourism.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j //Just for logging
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;

    private final AtomicInteger activeConnections = new AtomicInteger(0);


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent listener) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(listener.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("User " + username + " disconnected!");
        } else {
            log.info("User disconnected!");
        }
    }


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        int count = activeConnections.incrementAndGet();
        log.info("✅ New WebSocket connection. Active sessions: {}", count);

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        log.info("Session {} connected/disconnected", sessionId);

    }

}
