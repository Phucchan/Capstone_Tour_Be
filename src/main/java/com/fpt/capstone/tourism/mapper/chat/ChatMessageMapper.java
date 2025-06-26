package com.fpt.capstone.tourism.mapper.chat;

import com.fpt.capstone.tourism.dto.common.chat.ChatMessageShortDTO;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.chat.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        UserMapper.class
})
public interface ChatMessageMapper {
    ChatMessageShortDTO toChatMessageDTO(ChatMessage chatMessage);
}
