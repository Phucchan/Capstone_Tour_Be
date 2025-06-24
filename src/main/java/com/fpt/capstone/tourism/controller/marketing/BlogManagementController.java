package com.fpt.capstone.tourism.controller.marketing;

import com.fpt.capstone.tourism.dto.common.BlogManagerDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.BlogManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.BlogDetailManagerDTO;
import com.fpt.capstone.tourism.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marketing")
public class BlogManagementController {

    private final BlogService blogService;

    @GetMapping("/blogs")
    //postman http://localhost:8080/v1/marketing/blogs
    public ResponseEntity<GeneralResponse<PagingDTO<BlogManagerDTO>>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.getBlogs(page, size));
    }
    @GetMapping("/blogs/{id}")
    //postman http://localhost:8080/v1/marketing/blogs/1
    public ResponseEntity<GeneralResponse<BlogDetailManagerDTO>> getBlogDetail(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getBlog(id));
    }

    @PostMapping("/blogs")
    public ResponseEntity<GeneralResponse<BlogManagerDTO>> createBlog(@RequestBody BlogManagerRequestDTO requestDTO) {
        return ResponseEntity.ok(blogService.createBlog(requestDTO));
    }

    @PutMapping("/blogs/{id}")

    public ResponseEntity<GeneralResponse<BlogManagerDTO>> updateBlog(@PathVariable Long id,
                                                                      @RequestBody BlogManagerRequestDTO requestDTO) {
        return ResponseEntity.ok(blogService.updateBlog(id, requestDTO));
    }

    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<GeneralResponse<String>> deleteBlog(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.deleteBlog(id));
    }
}