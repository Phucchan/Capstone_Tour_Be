package com.fpt.capstone.tourism.model.blog;

import com.fpt.capstone.tourism.model.BaseEntity;
import com.fpt.capstone.tourism.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blog")
@EqualsAndHashCode(callSuper = true, exclude = {"author", "blogTags"})
@ToString(callSuper = true, exclude = {"author", "blogTags"})
public class Blog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thumbnail_image_url")
    private String thumbnailImageUrl;

    private String title;

    private String description;

    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "is_deleted")
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany
    @JoinTable(
            name = "blog_tag",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    private List<Tag> blogTags;
}
