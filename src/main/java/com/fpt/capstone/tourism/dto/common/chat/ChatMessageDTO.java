package com.fpt.capstone.tourism.dto.common.chat;


import com.fpt.capstone.tourism.model.enums.MessageType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ChatMessageDTO {
    private Long id;
    private String content;
    private LocalDateTime dateSent;
    private MessageType messageType;
    private Long chatGroupId;
    private Long userId;


}
