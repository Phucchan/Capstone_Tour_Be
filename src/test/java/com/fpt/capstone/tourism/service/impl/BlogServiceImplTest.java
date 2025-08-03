package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.BlogManagerDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.BlogManagerRequestDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BlogServiceImplTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private UserService userService;

    @Mock
    private BlogMapper blogMapper;

    @Captor
    private ArgumentCaptor<Blog> blogArgumentCaptor;

    @InjectMocks
    private BlogServiceImpl blogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Normal case: createBlog success
    @Test
    void createBlog_Success() {
        // Arrange
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(1L);
        req.setTitle("Test Title");
        req.setDescription("Test Description");
        req.setContent("Test Content");
        req.setThumbnailImageUrl("http://example.com/thumb.jpg");


        User author = User.builder().id(1L).build();
        Blog blog = new Blog();
        BlogManagerDTO dto = new BlogManagerDTO();

        when(userService.findById(1L)).thenReturn(author);
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);
        when(blogMapper.blogToBlogDTO(blog)).thenReturn(dto);

        // Act
        GeneralResponse<BlogManagerDTO> response = blogService.createBlog(req);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.BLOG_CREATE_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        verify(blogRepository, times(1)).save(any(Blog.class));
    }

    // Abnormal case: createBlog throws BusinessException when author not found
    @Test
    void createBlog_ThrowsBusinessException_WhenAuthorNotFound() {
        // Arrange
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(99L); // Non-existent author
        req.setTitle("A Valid Title");
        req.setDescription("Test Description");
        req.setThumbnailImageUrl("http://example.com/thumb.jpg");
        when(userService.findById(99L)).thenThrow(BusinessException.of(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.createBlog(req));
        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE, ex.getResponseMessage());
    }

    // Abnormal case: createBlog throws BusinessException when title is null
    @Test
    void createBlog_ThrowsBusinessException_WhenTitleIsNull() {
        // Arrange
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(1L);
        req.setTitle(null); // Null title
        req.setContent("Some valid content");
        req.setDescription("Test Description");
        req.setThumbnailImageUrl("http://example.com/thumb.jpg");

        // Arrange: Mock the dependencies
        when(userService.findById(1L)).thenReturn(User.builder().id(1L).build());
        // We simulate that the database layer would throw an exception for a mandatory field being null.
        when(blogRepository.save(any(Blog.class))).thenThrow(new RuntimeException("Simulated DB error for null title"));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.createBlog(req), "Should throw exception when title is null");
        assertEquals(Constants.Message.BLOG_CREATE_FAIL, ex.getResponseMessage());

        // Verify user was checked and save was attempted.
        verify(userService, times(1)).findById(1L);
        verify(blogRepository, times(1)).save(any(Blog.class));
    }

    // Abnormal case: createBlog throws BusinessException when content is null
    @Test
    void createBlog_ThrowsBusinessException_WhenContentIsNull() {
        // Arrange
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(1L);
        req.setTitle("A valid title");
        req.setContent(null); // Null content
        when(userService.findById(1L)).thenReturn(User.builder().id(1L).build());
        when(blogRepository.save(any(Blog.class))).thenThrow(new RuntimeException("Simulated DB error for null content"));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.createBlog(req), "Should throw exception when content is null");
        assertEquals(Constants.Message.BLOG_CREATE_FAIL, ex.getResponseMessage());
    }

    // Abnormal case: createBlog throws BusinessException when description is null
    @Test
    void createBlog_ThrowsBusinessException_WhenDescriptionIsNull() {
        // Arrange
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(1L);
        req.setTitle("A Valid Title");
        req.setContent("Valid Content");
        req.setDescription(null); // The invalid field

        when(userService.findById(1L)).thenReturn(User.builder().id(1L).build());
        when(blogRepository.save(any(Blog.class))).thenThrow(new RuntimeException("Simulated DB error for null description"));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.createBlog(req), "Should throw exception when description is null");
        assertEquals(Constants.Message.BLOG_CREATE_FAIL, ex.getResponseMessage());
    }

    // Abnormal case: createBlog throws BusinessException when thumbnailImageUrl is null
    @Test
    void createBlog_ThrowsBusinessException_WhenThumbnailIsNull() {
        // Arrange
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setAuthorId(1L);
        req.setTitle("A Valid Title");
        req.setContent("Valid Content");
        req.setThumbnailImageUrl(null); // The invalid field

        when(userService.findById(1L)).thenReturn(User.builder().id(1L).build());
        when(blogRepository.save(any(Blog.class))).thenThrow(new RuntimeException("Simulated DB error for null thumbnail"));

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.createBlog(req), "Should throw exception when thumbnail is null");
        assertEquals(Constants.Message.BLOG_CREATE_FAIL, ex.getResponseMessage());
    }

    // Normal case: updateBlog success
    @Test
    void updateBlog_Success() {
        // Arrange
        Long blogId = 1L;
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setTitle("New Title");
        req.setDescription("New Description");

        Blog existingBlog = Blog.builder().id(blogId).title("Old Title").description("Old Description").build();
        BlogManagerDTO dto = new BlogManagerDTO();

        when(blogRepository.findById(blogId)).thenReturn(Optional.of(existingBlog));
        when(blogRepository.save(any(Blog.class))).thenReturn(existingBlog);
        when(blogMapper.blogToBlogDTO(existingBlog)).thenReturn(dto);

        // Act
        GeneralResponse<BlogManagerDTO> response = blogService.updateBlog(blogId, req);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.BLOG_UPDATE_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        verify(blogRepository, times(1)).save(existingBlog);
        assertEquals("New Title", existingBlog.getTitle());
        assertEquals("New Description", existingBlog.getDescription());
    }

    // Abnormal case: updateBlog throws BusinessException when blog not found
    @Test
    void updateBlog_ThrowsBusinessException_WhenBlogNotFound() {
        // Arrange
        Long blogId = 99L; // Non-existent blog
        BlogManagerRequestDTO req = new BlogManagerRequestDTO();
        req.setTitle("New Title");

        when(blogRepository.findById(blogId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.updateBlog(blogId, req));
        assertEquals(Constants.Message.BLOG_NOT_FOUND, ex.getResponseMessage());
    }

    // Normal case: updateBlog should not update fields when their DTO values are null
    @Test
    void updateBlog_shouldNotUpdateFields_whenRequestFieldsAreNull() {
        // Arrange
        Long blogId = 1L;
        String oldTitle = "Old Title";
        String oldDescription = "Old Description";
        String oldContent = "Old Content";
        String oldThumbnail = "old_thumb.jpg";

        BlogManagerRequestDTO reqWithNulls = new BlogManagerRequestDTO();
        reqWithNulls.setTitle(null);
        reqWithNulls.setDescription(null);
        reqWithNulls.setContent(null);
        reqWithNulls.setThumbnailImageUrl(null);

        Blog existingBlog = Blog.builder().id(blogId).title(oldTitle).description(oldDescription).content(oldContent).thumbnailImageUrl(oldThumbnail).build();

        when(blogRepository.findById(blogId)).thenReturn(Optional.of(existingBlog));

        // Act: This call should not throw an exception
        assertDoesNotThrow(() -> blogService.updateBlog(blogId, reqWithNulls));

        // Assert
        verify(blogRepository).save(blogArgumentCaptor.capture());
        Blog savedBlog = blogArgumentCaptor.getValue();

        // Verify that the fields were NOT updated because the input was null
        assertEquals(oldTitle, savedBlog.getTitle(), "Title should not be updated for null input");
        assertEquals(oldDescription, savedBlog.getDescription(), "Description should not be updated for null input");
        assertEquals(oldContent, savedBlog.getContent(), "Content should not be updated for null input");
        assertEquals(oldThumbnail, savedBlog.getThumbnailImageUrl(), "Thumbnail should not be updated for null input");
    }
    // Normal case: deleteBlog success
    @Test
    void deleteBlog_Success() {
        // Arrange
        Long blogId = 1L;
        Blog existingBlog = Blog.builder()
                .id(blogId)
                .deleted(false) // Giá trị ban đầu
                .build();

        when(blogRepository.findById(blogId)).thenReturn(Optional.of(existingBlog));

        // Act
        GeneralResponse<String> response = blogService.deleteBlog(blogId);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.BLOG_DELETE_SUCCESS, response.getMessage());
        assertTrue(existingBlog.getDeleted());
        verify(blogRepository, times(1)).save(existingBlog);
    }

    // Abnormal case: deleteBlog throws BusinessException when blog not found
    @Test
    void deleteBlog_ThrowsBusinessException_WhenBlogNotFound() {
        // Arrange
        Long blogId = 99L; // Non-existent blog
        when(blogRepository.findById(blogId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> blogService.deleteBlog(blogId));
        assertEquals(Constants.Message.BLOG_NOT_FOUND, ex.getResponseMessage());
    }
}
