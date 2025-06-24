package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.BlogManagerDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.BlogManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.BlogDetailManagerDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final TagRepository tagRepository;
    private final UserService userService;
    private final BlogMapper blogMapper;


    @Override
    public PagingDTO<BlogSummaryDTO> getAllBlogs(Pageable pageable) {
        // 1. Gọi repository để lấy dữ liệu dạng Page<Blog>
        Page<Blog> blogPage = blogRepository.findByDeletedFalse(pageable);

        // 2. Chuyển đổi danh sách Blog entities sang danh sách BlogSummaryDTO
        List<BlogSummaryDTO> blogSummaries = blogPage.getContent().stream()
                .map(blogMapper::blogToBlogSummaryDTO)
                .collect(Collectors.toList());

        // 3. Xây dựng và trả về đối tượng PagingDTO
        return PagingDTO.<BlogSummaryDTO>builder()
                .page(blogPage.getNumber()) // Trang hiện tại
                .size(blogPage.getSize()) // Kích thước trang
                .total(blogPage.getTotalElements()) // Tổng số phần tử
                .items(blogSummaries) // Danh sách các mục
                .build();
    }

    @Override
    public GeneralResponse<BlogManagerDTO> createBlog(BlogManagerRequestDTO requestDTO) {
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
            BlogManagerDTO dto = blogMapper.blogToBlogDTO(saved);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.BLOG_CREATE_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.BLOG_CREATE_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<BlogManagerDTO> updateBlog(Long id, BlogManagerRequestDTO requestDTO) {
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
            BlogManagerDTO dto = blogMapper.blogToBlogDTO(saved);
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
    public GeneralResponse<PagingDTO<BlogManagerDTO>> getBlogs(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Blog> blogPage = blogRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable);
            List<BlogManagerDTO> dtos = blogPage.getContent().stream()
                    .map(blogMapper::blogToBlogDTO)
                    .toList();
            PagingDTO<BlogManagerDTO> pagingDTO = PagingDTO.<BlogManagerDTO>builder()
                    .page(blogPage.getNumber())
                    .size(blogPage.getSize())
                    .total(blogPage.getTotalElements())
                    .items(dtos)
                    .build();
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.BLOG_LIST_SUCCESS, pagingDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.BLOG_LIST_FAIL, ex);
        }
    }
    @Override
    public GeneralResponse<BlogDetailManagerDTO> getBlog(Long id) {
        try {
            Blog blog = blogRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.BLOG_NOT_FOUND));
            BlogDetailManagerDTO dto = blogMapper.blogToBlogDetailDTO(blog);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.BLOG_DETAIL_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.BLOG_DETAIL_FAIL, ex);
        }
    }
}