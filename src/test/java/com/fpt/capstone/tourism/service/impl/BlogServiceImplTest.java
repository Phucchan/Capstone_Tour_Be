package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.BlogManagerDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.BlogManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.blog.Tag;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    // =================================================================

    @Test
    @DisplayName("[getAllBlogs] Valid Input: Lấy danh sách blog thành công khi có dữ liệu")
    void getAllBlogs_whenBlogsExist_shouldReturnPagingDTOWithData() {
        System.out.println("Test Case: Valid Input - Lấy danh sách blog thành công khi có dữ liệu.");
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // 1. Tạo dữ liệu giả
        // Blog 1 có 2 tag
        Blog blogWithTags = Blog.builder().id(1L).title("Blog with Tags").build();
        // SỬA LỖI: Sử dụng builder để tạo đối tượng Tag, đây là cách làm an toàn và đúng đắn hơn.
        blogWithTags.setBlogTags(List.of(
                Tag.builder().id(1L).name("Travel").build(),
                Tag.builder().id(2L).name("Asia").build()
        ));

        // Blog 2 không có tag (list tag là null)
        Blog blogWithNullTags = Blog.builder().id(2L).title("Blog with Null Tags").build();
        blogWithNullTags.setBlogTags(null);

        // Blog 3 có list tag rỗng
        Blog blogWithEmptyTags = Blog.builder().id(3L).title("Blog with Empty Tags").build();
        blogWithEmptyTags.setBlogTags(Collections.emptyList());

        List<Blog> blogList = List.of(blogWithTags, blogWithNullTags, blogWithEmptyTags);
        Page<Blog> blogPage = new PageImpl<>(blogList, pageable, blogList.size());

        // 2. Giả lập hành vi của các dependency
        when(blogRepository.findByDeletedFalse(pageable)).thenReturn(blogPage);

        // Giả lập mapper cho từng blog. Service sẽ tự thêm các tag vào DTO này.
        when(blogMapper.blogToBlogSummaryDTO(blogWithTags)).thenReturn(
                BlogSummaryDTO.builder().id(1L).title("Blog with Tags").tags(new ArrayList<>()).build()
        );
        when(blogMapper.blogToBlogSummaryDTO(blogWithNullTags)).thenReturn(
                BlogSummaryDTO.builder().id(2L).title("Blog with Null Tags").tags(new ArrayList<>()).build()
        );
        when(blogMapper.blogToBlogSummaryDTO(blogWithEmptyTags)).thenReturn(
                BlogSummaryDTO.builder().id(3L).title("Blog with Empty Tags").tags(new ArrayList<>()).build()
        );

        // Act
        PagingDTO<BlogSummaryDTO> result = blogService.getAllBlogs(pageable);

        // Assert
        assertNotNull(result, "Kết quả không được là null.");
        assertEquals(3, result.getTotal(), "Tổng số lượng blog phải là 3.");
        assertEquals(3, result.getItems().size(), "Danh sách blog trả về phải có 3 phần tử.");

        // Kiểm tra chi tiết từng DTO
        BlogSummaryDTO dto1 = result.getItems().get(0);
        assertEquals("Blog with Tags", dto1.getTitle());
        assertNotNull(dto1.getTags());
        assertEquals(2, dto1.getTags().size(), "Blog 1 phải có 2 tag.");
        assertTrue(dto1.getTags().contains("Travel"), "Phải chứa tag 'Travel'.");

        BlogSummaryDTO dto2 = result.getItems().get(1);
        assertEquals("Blog with Null Tags", dto2.getTitle());
        assertNotNull(dto2.getTags(), "Danh sách tag không được là null.");
        assertTrue(dto2.getTags().isEmpty(), "Blog 2 phải có danh sách tag rỗng.");

        BlogSummaryDTO dto3 = result.getItems().get(2);
        assertEquals("Blog with Empty Tags", dto3.getTitle());
        assertNotNull(dto3.getTags());
        assertTrue(dto3.getTags().isEmpty(), "Blog 3 phải có danh sách tag rỗng.");

        // Verify
        verify(blogRepository, times(1)).findByDeletedFalse(pageable);
        verify(blogMapper, times(3)).blogToBlogSummaryDTO(any(Blog.class));
        System.out.println("Log: " + Constants.Message.BLOG_LIST_SUCCESS);
    }

    @Test
    @DisplayName("[getAllBlogs] Valid Input: Trả về trang rỗng khi không có blog nào")
    void getAllBlogs_whenNoBlogsExist_shouldReturnEmptyPagingDTO() {
        System.out.println("Test Case: Valid Input - Trả về trang rỗng khi không có blog nào.");
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        // Giả lập repository trả về một trang rỗng
        when(blogRepository.findByDeletedFalse(pageable)).thenReturn(Page.empty(pageable));

        // Act
        PagingDTO<BlogSummaryDTO> result = blogService.getAllBlogs(pageable);

        // Assert
        assertNotNull(result, "Kết quả không được là null.");
        assertEquals(0, result.getTotal(), "Tổng số lượng blog phải là 0.");
        assertTrue(result.getItems().isEmpty(), "Danh sách blog trả về phải rỗng.");

        // Verify
        verify(blogRepository, times(1)).findByDeletedFalse(pageable);
        // Mapper không bao giờ được gọi vì không có blog nào để chuyển đổi
        verify(blogMapper, never()).blogToBlogSummaryDTO(any());
        System.out.println("Log: " + Constants.Message.SUCCESS + ". " + Constants.Message.NO_SERVICES_AVAILABLE);
    }

    @Test
    @DisplayName("[getAllBlogs] Invalid Input: Thất bại khi repository ném ra lỗi")
    void getAllBlogs_whenRepositoryFails_shouldPropagateException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi repository ném ra lỗi.");
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        // Giả lập repository ném ra một lỗi runtime (ví dụ: mất kết nối DB)
        when(blogRepository.findByDeletedFalse(pageable)).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        // Vì phương thức không có khối try-catch, exception sẽ được ném ra ngoài
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            blogService.getAllBlogs(pageable);
        });

        assertEquals("Database connection error", exception.getMessage(), "Thông báo lỗi phải khớp.");

        // Verify
        verify(blogRepository, times(1)).findByDeletedFalse(pageable);
        verify(blogMapper, never()).blogToBlogSummaryDTO(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.BLOG_LIST_FAIL);
    }

}