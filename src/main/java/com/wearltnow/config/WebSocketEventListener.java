package com.wearltnow.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {
    private final ConcurrentHashMap<String, Integer> activeTopics = new ConcurrentHashMap<>();

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        if (destination != null && destination.startsWith("/topic")) {
            activeTopics.merge(destination, 1, Integer::sum);
            System.out.println("Subscriber added to topic: " + destination);
        }
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        // Giảm số lượng subscriber cho topic (cần ánh xạ topic với sessionId nếu phức tạp hơn)
        activeTopics.forEach((key, count) -> {
            if (count > 0) {
                activeTopics.merge(key, -1, Integer::sum);
                if (activeTopics.get(key) == 0) {
                    activeTopics.remove(key);
                    System.out.println("Topic deactivated: " + key);
                }
            }
        });
    }

    // Kiểm tra một topic có active hay không
    public boolean isTopicActive(String topic) {
        return activeTopics.getOrDefault(topic, 0) > 0;
    }
    public ConcurrentHashMap.KeySetView<String, Integer> getActiveTopics() {
        return activeTopics.keySet();
    }

}

