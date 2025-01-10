package com.wearltnow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // STOMP endpoint
                .setAllowedOrigins("*") // Cho phép mọi nguồn kết nối
                .withSockJS(); // Cung cấp SockJS cho các kết nối WebSocket fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Cấu hình prefix cho các destinations (mà ứng dụng sẽ gửi tới)
        registry.setApplicationDestinationPrefixes("/app");
        // Cấu hình các destination mà broker sẽ xử lý
        registry.enableSimpleBroker("/chatroom", "user", "/topic");
        // Cấu hình user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }
}
