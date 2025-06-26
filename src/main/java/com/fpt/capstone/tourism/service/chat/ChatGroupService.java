package com.fpt.capstone.tourism.service.chat;

import com.fpt.capstone.tourism.dto.common.chat.ChatGroupWithLastMessageDTO;

import java.util.List;

public interface ChatGroupService {
    List<ChatGroupWithLastMessageDTO> getChatGroupsByUserId(Long userId);
}
