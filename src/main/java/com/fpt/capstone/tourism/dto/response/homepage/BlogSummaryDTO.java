package com.fpt.capstone.tourism.dto.response.homepage;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class BlogSummaryDTO {
    /**
     * The unique identifier for the blog post.
     */
    private Long id;

    /**
     * The title of the blog post.
     */
    private String title;

    /**
     * The URL for the blog post's thumbnail image.
     */
    private String thumbnailImageUrl;

    /**
     * The name of the author.
     */
    private String authorName;

    /**
     * The creation date of the blog post.
     */
    private LocalDateTime createdAt;
}
