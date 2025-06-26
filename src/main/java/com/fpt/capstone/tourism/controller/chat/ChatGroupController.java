package com.fpt.capstone.tourism.controller.chat;

import com.fpt.capstone.tourism.dto.common.chat.ChatGroupWithLastMessageDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.service.chat.ChatGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/chat-group")
public class ChatGroupController {

    private final ChatGroupService chatGroupService;


    @GetMapping("/list/{userId}")
    public ResponseEntity<GeneralResponse<List<ChatGroupWithLastMessageDTO>>> getChatGroupsByUserId(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(GeneralResponse.of(chatGroupService.getChatGroupsByUserId(userId)));
    }


}
