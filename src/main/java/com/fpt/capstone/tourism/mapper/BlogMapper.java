package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.common.BlogDTO;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.blog.Tag;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlogMapper {
    @Mapping(source = "author.fullName", target = "authorName")
    BlogSummaryDTO blogToBlogSummaryDTO(Blog blog);

    @Mapping(source = "author.fullName", target = "authorName")
    @Mapping(target = "tags", expression = "java(mapTags(blog.getBlogTags()))")
    BlogDTO blogToBlogDTO(Blog blog);

    default List<String> mapTags(List<Tag> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags.stream().map(Tag::getName).collect(Collectors.toList());
    }
}