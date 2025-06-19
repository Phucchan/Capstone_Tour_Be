package com.fpt.capstone.tourism.model;

import com.fpt.capstone.tourism.model.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friendships")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"sender", "receiver"})
@ToString(callSuper = true, exclude = {"sender", "receiver"})
public class Friendship extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người gửi lời mời kết bạn
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // Người nhận lời mời kết bạn
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

}
