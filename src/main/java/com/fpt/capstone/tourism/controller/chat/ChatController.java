package com.fpt.capstone.tourism.controller.chat;

import com.fpt.capstone.tourism.dto.common.UserDTO;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@RequestMapping("/public/chat")
@Slf4j
public class ChatController {


    private final ChatService chatService;

    @MessageMapping("/user/connect") //Receives messages from clients sending to /app/user/connect
    @SendTo("/topic/active") //Send the response to clients subscribed to topic/active
    public UserBasicDTO connect(UserDTO userDTO) {
        log.info("User connected: {}", userDTO.getUsername());
        return chatService.connect(userDTO);
    }

    @MessageMapping("/user/disconnect") //Receives messages from clients sending to /app/user/connect
    @SendTo("/topic/active") //Send the response to clients subscribed to topic/active
    public UserBasicDTO disconnect(UserBasicDTO userDTO) {
        return chatService.disconnect(userDTO);
    }
}
