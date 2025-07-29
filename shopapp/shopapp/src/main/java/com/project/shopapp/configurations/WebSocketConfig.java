package com.project.shopapp.configurations;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // Broker nội bộ để client subscribe nhận tin nhắn
        registry.setApplicationDestinationPrefixes("/app"); //Prefix cho các endpoint client gửi lên server xử lý
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket với SockJS (fallback)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}