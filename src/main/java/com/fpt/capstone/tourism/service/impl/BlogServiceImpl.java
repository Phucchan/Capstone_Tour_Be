package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.BlogDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.BlogRequestDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.blog.Tag;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.repository.blog.TagRepository;
import com.fpt.capstone.tourism.service.BlogService;
import com.fpt.capstone.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final TagRepository tagRepository;
    private final UserService userService;
    private final BlogMapper blogMapper;

    @Override
    public GeneralResponse<BlogDTO> createBlog(BlogRequestDTO requestDTO) {
        try {
            User author = userService.findById(requestDTO.getAuthorId());
            Blog blog = Blog.builder()
                    .title(requestDTO.getTitle())
                    .description(requestDTO.getDescription())
                    .content(requestDTO.getContent())
                    .thumbnailImageUrl(requestDTO.getThumbnailImageUrl())
                    .author(author)
                    .deleted(false)
                    .build();

            if (requestDTO.getTagIds() != null) {
                List<Tag> tags = tagRepository.findAllById(requestDTO.getTagIds());
                blog.setBlogTags(tags);
            }

            Blog saved = blogRepository.save(blog);
            BlogDTO dto = blogMapper.blogToBlogDTO(saved);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.BLOG_CREATE_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.BLOG_CREATE_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<BlogDTO> updateBlog(Long id, BlogRequestDTO requestDTO) {
        try {
            Blog blog = blogRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.BLOG_NOT_FOUND));

            if (requestDTO.getTitle() != null) blog.setTitle(requestDTO.getTitle());
            if (requestDTO.getDescription() != null) blog.setDescription(requestDTO.getDescription());
            if (requestDTO.getContent() != null) blog.setContent(requestDTO.getContent());
            if (requestDTO.getThumbnailImageUrl() != null) blog.setThumbnailImageUrl(requestDTO.getThumbnailImageUrl());
            if (requestDTO.getAuthorId() != null) {
                User author = userService.findById(requestDTO.getAuthorId());
                blog.setAuthor(author);
            }
            if (requestDTO.getTagIds() != null) {
                List<Tag> tags = tagRepository.findAllById(requestDTO.getTagIds());
                blog.setBlogTags(tags);
            }

            Blog saved = blogRepository.save(blog);
            BlogDTO dto = blogMapper.blogToBlogDTO(saved);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.BLOG_UPDATE_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.BLOG_UPDATE_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<String> deleteBlog(Long id) {
        try {
            Blog blog = blogRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.BLOG_NOT_FOUND));
            blog.softDelete();
            blogRepository.save(blog);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.BLOG_DELETE_SUCCESS, null);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.BLOG_DELETE_FAIL, ex);
        }
    }
    @Override
    public GeneralResponse<List<BlogDTO>> getBlogs() {
        try {
            List<Blog> blogs = blogRepository.findByDeletedFalseOrderByCreatedAtDesc();
            List<BlogDTO> dtos = blogs.stream()
                    .map(blogMapper::blogToBlogDTO)
                    .toList();
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.BLOG_LIST_SUCCESS, dtos);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.BLOG_LIST_FAIL, ex);
        }
    }
}