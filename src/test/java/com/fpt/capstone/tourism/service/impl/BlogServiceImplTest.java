package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.BlogManagerDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.BlogManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.BlogDetailDTO;
import com.fpt.capstone.tourism.dto.response.BlogDetailManagerDTO;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.blog.Tag;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.repository.blog.TagRepository;
import com.fpt.capstone.tourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlogServiceImplTest {

    @Mock
    private BlogRepository blogRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private UserService userService;
    @Mock
    private BlogMapper blogMapper;

    @InjectMocks
    private BlogServiceImpl blogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Normal case: getAllBlogs returns correct paging DTO
    @Test
    void getAllBlogs_ReturnsPagingDTO() {
        Blog blog = Blog.builder().id(1L).title("Test").blogTags(List.of(Tag.builder().name("tag1").build())).build();
        BlogSummaryDTO summaryDTO = new BlogSummaryDTO();
        summaryDTO.setTitle("Test");
        summaryDTO.setTags(List.of("tag1"));

        Page<Blog> blogPage = new PageImpl<>(List.of(blog), PageRequest.of(0, 10), 1);
        when(blogRepository.findByDeletedFalse(any())).thenReturn(blogPage);
        when(blogMapper.blogToBlogSummaryDTO(blog)).thenReturn(summaryDTO);

        PagingDTO<BlogSummaryDTO> result = blogService.getAllBlogs(PageRequest.of(0, 10));
        assertEquals(1, result.getItems().size());
        assertEquals("Test", result.getItems().get(0).getTitle());
        assertEquals(List.of("tag1"), result.getItems().get(0).getTags());
    }

    // Normal case: createBlog success
    @Test
    void createBlog_Success() {
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(1L);
        req.setTitle("Title");
        req.setDescription("Desc");
        req.setContent("Content");
        req.setThumbnailImageUrl("url");
        req.setTagIds(List.of(1L));

        User author = User.builder().id(1L).build();
        Blog blog = Blog.builder().title("Title").author(author).build();
        Blog savedBlog = Blog.builder().id(1L).title("Title").author(author).build();
        BlogManagerDTO dto = new BlogManagerDTO();

        when(userService.findById(1L)).thenReturn(author);
        when(tagRepository.findAllById(List.of(1L))).thenReturn(List.of(Tag.builder().id(1L).name("tag1").build()));
        when(blogRepository.save(any(Blog.class))).thenReturn(savedBlog);
        when(blogMapper.blogToBlogDTO(savedBlog)).thenReturn(dto);

        GeneralResponse<BlogManagerDTO> response = blogService.createBlog(req);
        assertEquals(200, response.getStatus());
        assertEquals(Constants.Message.BLOG_CREATE_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
    }

    // Abnormal case: createBlog throws BusinessException from userService
    @Test
    void createBlog_ThrowsBusinessException_WhenUserNotFound() {
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(99L);

        when(userService.findById(99L)).thenThrow(BusinessException.of(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE));

        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.createBlog(req));
        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE, ex.getResponseMessage());
    }

    // Abnormal case: createBlog throws BusinessException on unexpected error
    @Test
    void createBlog_ThrowsBusinessException_OnError() {
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(1L);

        when(userService.findById(1L)).thenThrow(new RuntimeException("DB error"));

        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.createBlog(req));
        assertEquals(Constants.Message.BLOG_CREATE_FAIL, ex.getResponseMessage());
    }

    // Normal case: updateBlog success
    @Test
    void updateBlog_Success() {
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setTitle("New Title");
        Blog blog = Blog.builder().id(1L).title("Old Title").build();
        Blog savedBlog = Blog.builder().id(1L).title("New Title").build();
        BlogManagerDTO dto = new BlogManagerDTO();

        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(blogRepository.save(blog)).thenReturn(savedBlog);
        when(blogMapper.blogToBlogDTO(savedBlog)).thenReturn(dto);

        GeneralResponse<BlogManagerDTO> response = blogService.updateBlog(1L, req);
        assertEquals(200, response.getStatus());
        assertEquals(Constants.Message.BLOG_UPDATE_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
    }

    // Abnormal case: updateBlog not found
    @Test
    void updateBlog_ThrowsBusinessException_WhenNotFound() {
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        when(blogRepository.findById(2L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.updateBlog(2L, req));
        assertEquals(Constants.Message.BLOG_NOT_FOUND, ex.getResponseMessage());
    }

    // Abnormal case: updateBlog unexpected error
    @Test
    void updateBlog_ThrowsBusinessException_OnError() {
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        Blog blog = Blog.builder().id(1L).build();
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(blogRepository.save(blog)).thenThrow(new RuntimeException("DB error"));

        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.updateBlog(1L, req));
        assertEquals(Constants.Message.BLOG_UPDATE_FAIL, ex.getResponseMessage());
    }

    // Normal case: deleteBlog success
    @Test
    void deleteBlog_Success() {
        Blog blog = Blog.builder().id(1L).deleted(false).build();
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(blogRepository.save(blog)).thenReturn(blog);

        GeneralResponse<String> response = blogService.deleteBlog(1L);
        assertEquals(200, response.getStatus());
        assertEquals(Constants.Message.BLOG_DELETE_SUCCESS, response.getMessage());
        assertNull(response.getData());
        assertTrue(blog.getDeleted());
    }

    // Abnormal case: deleteBlog not found
    @Test
    void deleteBlog_ThrowsBusinessException_WhenNotFound() {
        when(blogRepository.findById(2L)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.deleteBlog(2L));
        assertEquals(Constants.Message.BLOG_NOT_FOUND, ex.getResponseMessage());
    }

    // Abnormal case: deleteBlog unexpected error
    @Test
    void deleteBlog_ThrowsBusinessException_OnError() {
        Blog blog = Blog.builder().id(1L).build();
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(blogRepository.save(blog)).thenThrow(new RuntimeException("DB error"));

        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.deleteBlog(1L));
        assertEquals(Constants.Message.BLOG_DELETE_FAIL, ex.getResponseMessage());
    }

    // Normal case: getBlogs returns paging DTO
    @Test
    void getBlogs_ReturnsPagingDTO() {
        Blog blog = Blog.builder().id(1L).title("Test").build();
        BlogManagerDTO dto = new BlogManagerDTO();
        Page<Blog> blogPage = new PageImpl<>(List.of(blog), PageRequest.of(0, 10), 1);

        when(blogRepository.findByDeletedFalseOrderByCreatedAtDesc(any())).thenReturn(blogPage);
        when(blogMapper.blogToBlogDTO(blog)).thenReturn(dto);

        GeneralResponse<PagingDTO<BlogManagerDTO>> response = blogService.getBlogs(0, 10);
        assertEquals(200, response.getStatus());
        assertEquals(Constants.Message.BLOG_LIST_SUCCESS, response.getMessage());
        assertEquals(1, response.getData().getItems().size());
    }

    // Abnormal case: getBlogs unexpected error
    @Test
    void getBlogs_ThrowsBusinessException_OnError() {
        when(blogRepository.findByDeletedFalseOrderByCreatedAtDesc(any())).thenThrow(new RuntimeException("DB error"));
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.getBlogs(0, 10));
        assertEquals(Constants.Message.BLOG_LIST_FAIL, ex.getResponseMessage());
    }

    // Normal case: getBlog returns detail DTO
    @Test
    void getBlog_ReturnsDetailDTO() {
        Blog blog = Blog.builder().id(1L).build();
        BlogDetailManagerDTO dto = new BlogDetailManagerDTO();
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(blogMapper.blogToBlogDetailDTO(blog)).thenReturn(dto);

        GeneralResponse<BlogDetailManagerDTO> response = blogService.getBlog(1L);
        assertEquals(200, response.getStatus());
        assertEquals(Constants.Message.BLOG_DETAIL_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
    }

    // Abnormal case: getBlog not found
    @Test
    void getBlog_ThrowsBusinessException_WhenNotFound() {
        when(blogRepository.findById(2L)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.getBlog(2L));
        assertEquals(Constants.Message.BLOG_NOT_FOUND, ex.getResponseMessage());
    }

    // Abnormal case: getBlog unexpected error
    @Test
    void getBlog_ThrowsBusinessException_OnError() {
        Blog blog = Blog.builder().id(1L).build();
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(blogMapper.blogToBlogDetailDTO(blog)).thenThrow(new RuntimeException("DB error"));

        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.getBlog(1L));
        assertEquals(Constants.Message.BLOG_DETAIL_FAIL, ex.getResponseMessage());
    }

    // Boundary case: getBlogDetailById - blog is deleted
    @Test
    void getBlogDetailById_ThrowsBusinessException_WhenDeleted() {
        Blog blog = Blog.builder().id(1L).deleted(true).build();
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.getBlogDetailById(1L));
        assertEquals(Constants.Message.BLOG_NOT_FOUND, ex.getResponseMessage());
    }

    // Normal case: getBlogDetailById returns DTO
    @Test
    void getBlogDetailById_ReturnsDTO() {
        Blog blog = Blog.builder().id(1L).deleted(false).build();
        BlogDetailDTO dto = new BlogDetailDTO();
        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));
        when(blogMapper.blogToBlogDetailCustomerDTO(blog)).thenReturn(dto);

        BlogDetailDTO result = blogService.getBlogDetailById(1L);
        assertNotNull(result);
    }

    // Abnormal case: getBlogDetailById not found
    @Test
    void getBlogDetailById_ThrowsBusinessException_WhenNotFound() {
        when(blogRepository.findById(2L)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.getBlogDetailById(2L));
        assertEquals(Constants.Message.BLOG_NOT_FOUND, ex.getResponseMessage());
    }
}