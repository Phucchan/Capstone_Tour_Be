package com.fpt.capstone.tourism.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogManagerRequestDTO {
    private String title;
    private String description;
    private String content;
    private String thumbnailImageUrl;
    private Long authorId;
    private List<Long> tagIds;
}