package com.fpt.capstone.tourism.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "user")
@ToString(callSuper = true, exclude = "user")
public class UserPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_point_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer points = 0;
}
