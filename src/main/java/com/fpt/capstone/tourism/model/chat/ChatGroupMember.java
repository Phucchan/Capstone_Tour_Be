package com.fpt.capstone.tourism.model.chat;


import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "chat_group_members")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChatGroupMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ChatGroup chatGroup;

    private boolean admin;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

}
