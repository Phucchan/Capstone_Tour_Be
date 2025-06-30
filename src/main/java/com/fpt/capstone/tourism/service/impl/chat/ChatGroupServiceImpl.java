package com.fpt.capstone.tourism.service.impl.chat;

import com.fpt.capstone.tourism.dto.common.chat.ChatGroupWithLastMessageDTO;
import com.fpt.capstone.tourism.model.chat.ChatGroup;
import com.fpt.capstone.tourism.model.enums.MessageType;
import com.fpt.capstone.tourism.model.enums.UserStatus;
import com.fpt.capstone.tourism.repository.chat.ChatGroupRepository;
import com.fpt.capstone.tourism.service.chat.ChatGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGroupServiceImpl implements ChatGroupService {
    private final ChatGroupRepository chatGroupRepository;



    @Override
    public List<ChatGroupWithLastMessageDTO> getChatGroupsByUserId(Long userId) {
        log.info("Fetching chat groups with last message for user ID: {}", userId);
        List<Object[]> rows = chatGroupRepository.findChatGroupsWithLastMessageByUserId(userId);
        log.info("Found {} chat groups for user ID: {}", rows.size(), userId);

        return rows.stream().map(row -> new ChatGroupWithLastMessageDTO(
                ((Number) row[0]).longValue(),             // cg.id
                (String) row[1],                           // cg.name
                (Boolean) row[2],                          // cg.grouped
                row[3] != null ? ((Number) row[3]).longValue() : null, // cg.created_by
                (String) row[4],                           // cd.image_url

                row[5] != null ? ((Number) row[5]).longValue() : null,// messageId
                (String) row[6],                           // content
                row[7] != null ? ((Timestamp) row[7]).toLocalDateTime() : null, // dateSent
                row[8] != null ? MessageType.valueOf((String) row[8]) : null,   // messageType

                row[9] != null ? ((Number) row[9]).longValue() : null,     // senderId
                (String) row[10],                                           // fullName
                (String) row[11],                                          // username
                (String) row[12],                                          // email
                (String) row[13],                                          // avatarImage
                row[14] != null ? UserStatus.valueOf((String) row[14]) : null  // userStatus
        )).toList();
    }

}
