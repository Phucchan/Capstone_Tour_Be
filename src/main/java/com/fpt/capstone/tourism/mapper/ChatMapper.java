package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.common.chat.ChatGroupWithLastMessageDTO;
import com.fpt.capstone.tourism.dto.common.chat.ChatGroupMemberDTO;
import com.fpt.capstone.tourism.dto.common.chat.ChatMessageDTO;
import com.fpt.capstone.tourism.model.chat.ChatGroup;
import com.fpt.capstone.tourism.model.chat.ChatGroupMember;
import com.fpt.capstone.tourism.model.chat.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMapper {
    @Mapping(target = "user.id", source = "userId")
    ChatGroup toChatGroupEntity(ChatGroupWithLastMessageDTO chatGroupWithLastMessageDTO);

    @Mapping(target = "userId", source = "user.id")
    ChatGroupWithLastMessageDTO toChatGroupDTO(ChatGroup chatGroup);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "chatGroup.id", source = "chatGroupId")
    ChatMessage toChatMessage(ChatMessageDTO chatMessageDTO);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "chatGroupId", source = "chatGroup.id")
    ChatMessageDTO toChatMessageDTO(ChatMessage chatMessageDTO);


    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "chatGroup.id", source = "chatGroupId")
    ChatGroupMember toChatGroupMember(ChatGroupMemberDTO chatGroupMemberDTO);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "chatGroupId", source = "chatGroup.id")
    ChatGroupMemberDTO toChatGroupMemberDTO(ChatGroupMember chatGroupMember);



}
