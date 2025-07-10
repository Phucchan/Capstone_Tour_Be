package com.fpt.capstone.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String thumbnailImageUrl;
    private String authorName;
    private LocalDateTime createdAt;
    private List<String> tags;
}