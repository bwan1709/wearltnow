package com.wearltnow.controller;

import com.wearltnow.config.WebSocketEventListener;
import com.wearltnow.dto.request.message.MessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @Autowired
    private WebSocketEventListener webSocketEventListener;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/active-topics")
    public Set<String> getActiveTopics() {
        return webSocketEventListener.getActiveTopics();
    }


    @MessageMapping("/send-message")
    public void sendMessageToAdmin(@RequestBody MessageRequest messageRequest) {
        String adminTopic = "/topic/admin";
        messagingTemplate.convertAndSend(adminTopic, messageRequest.getMessage());
    }
}