package com.fpt.capstone.tourism.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "location")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(columnDefinition = "text")
    private String image;
    @Column(name = "is_deleted")
    private Boolean deleted;

}
