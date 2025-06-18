package com.fpt.capstone.tourism.repository.chat;

import com.fpt.capstone.tourism.model.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
