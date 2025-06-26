package com.fpt.capstone.tourism.model.chat;


import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Table(name = "chat_groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChatGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean grouped;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "chatGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ChatMessage> chatMessages;


}
