package com.fpt.capstone.tourism.dto.common.chat;


import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.model.enums.MessageType;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGroupWithLastMessageDTO {
    private Long id;
    private String name;
    private boolean grouped;
    private String imageUrl;
    private Long userId;
    private ChatMessageShortDTO lastChatMessage;

    public ChatGroupWithLastMessageDTO(
            Long id, String name, boolean grouped, Long userId, String imageUrl,
            Long messageId, String content, LocalDateTime dateSent, MessageType messageType,
            Long senderId, String fullName, String username, String email, String avatarImage, UserStatus status
    ) {
        this.id = id;
        this.name = name;
        this.grouped = grouped;
        this.userId = userId;
        this.imageUrl = imageUrl;

        if (messageId != null) {
            UserBasicDTO sender = UserBasicDTO.builder()
                    .id(senderId)
                    .fullName(fullName)
                    .username(username)
                    .email(email)
                    .avatarImage(avatarImage)
                    .userStatus(status)
                    .build();

            this.lastChatMessage = ChatMessageShortDTO.builder()
                    .id(messageId)
                    .content(content)
                    .dateSent(dateSent)
                    .messageType(messageType)
                    .user(sender)
                    .build();
        }
    }
}
