package com.fpt.capstone.tourism.dto.common.chat;

import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.model.enums.MessageType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageShortDTO {
    private Long id;
    private String content;
    private LocalDateTime dateSent;
    private MessageType messageType;
    private UserBasicDTO user;
}
