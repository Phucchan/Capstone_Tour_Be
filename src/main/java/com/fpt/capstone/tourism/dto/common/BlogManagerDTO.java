package com.fpt.capstone.tourism.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogManagerDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnailImageUrl;
    private String authorName;
    private List<String> tags;
    private Boolean deleted;
}
