package com.fpt.capstone.tourism.model.blog;

import com.fpt.capstone.tourism.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tag")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Tag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name")
    private String name;

    @Column
    private String description;

    @Column(name = "is_deleted")
    private Boolean deleted;

    @ManyToMany(mappedBy = "blogTags")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Blog> blogs;

}
