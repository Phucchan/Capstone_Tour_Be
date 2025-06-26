package com.fpt.capstone.tourism.repository.chat;

import com.fpt.capstone.tourism.dto.common.chat.ChatGroupWithLastMessageDTO;
import com.fpt.capstone.tourism.model.chat.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {

    @Query(value = """
    SELECT 
        cg.id AS id,
        cg.name AS name,
        cg.grouped AS grouped,
        cg.created_by AS userId,
        cg.image_url AS imageUrl,
        cm.id AS messageId,
        cm.content AS content,
        cm.date_sent AS dateSent,
        cm.message_type AS messageType,
        cm.user_id AS senderId,
        cm.full_name AS fullName,
        cm.username AS username,
        cm.email AS email,
        cm.avatar_img AS avatarImage,
        cm.user_status AS userStatus
    FROM chat_groups cg
    JOIN chat_group_members cgm ON cg.id = cgm.group_id
    LEFT JOIN LATERAL (
        SELECT cms.id , content, date_sent, message_type, u.id as user_id, u.full_name, u.username, u.email, u.avatar_img, u.user_status
        FROM chat_messages cms
        JOIN users u ON cms.user_id = u.id
        WHERE group_id = cg.id
        ORDER BY date_sent DESC
        LIMIT 1
    ) cm ON true
    WHERE cgm.user_id = :userId
    ORDER BY cm.date_sent DESC NULLS LAST
    """, nativeQuery = true)
    List<Object[]> findChatGroupsWithLastMessageByUserId(@Param("userId") Long userId);
}
