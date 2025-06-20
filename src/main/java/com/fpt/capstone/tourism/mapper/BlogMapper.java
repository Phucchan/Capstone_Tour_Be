package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.model.blog.Blog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlogMapper {
    @Mapping(source = "author.fullName", target = "authorName")
    BlogSummaryDTO blogToBlogSummaryDTO(Blog blog);
}