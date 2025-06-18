package com.fpt.capstone.tourism.repository.chat;

import com.fpt.capstone.tourism.model.chat.ChatGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatGroupMemberRepository extends JpaRepository<ChatGroupMember, Long> {
}
