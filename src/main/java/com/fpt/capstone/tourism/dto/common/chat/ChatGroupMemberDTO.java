package com.fpt.capstone.tourism.dto.common.chat;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatGroupMemberDTO {
    private Long id;
    private Long userId;
    private Long chatGroupId;
    private boolean admin;
    private LocalDateTime lastSeen;
}
