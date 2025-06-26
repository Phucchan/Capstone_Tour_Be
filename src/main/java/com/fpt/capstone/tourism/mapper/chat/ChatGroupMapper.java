package com.fpt.capstone.tourism.mapper.chat;

import com.fpt.capstone.tourism.dto.common.chat.ChatGroupWithLastMessageDTO;
import com.fpt.capstone.tourism.model.chat.ChatGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        ChatMessageMapper.class
})
public interface ChatGroupMapper {

    @Mapping(source = "user.id", target = "userId")
    ChatGroupWithLastMessageDTO toChatGroupDTO(ChatGroup chatGroup);
}
