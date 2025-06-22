package com.fpt.capstone.tourism.controller.marketing;

import com.fpt.capstone.tourism.dto.common.BlogDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.BlogRequestDTO;
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
    public ResponseEntity<GeneralResponse<List<BlogDTO>>> getBlogs() {
        return ResponseEntity.ok(blogService.getBlogs());
    }

    @PostMapping("/blogs")
    public ResponseEntity<GeneralResponse<BlogDTO>> createBlog(@RequestBody BlogRequestDTO requestDTO) {
        return ResponseEntity.ok(blogService.createBlog(requestDTO));
    }

    @PutMapping("/blogs/{id}")
    public ResponseEntity<GeneralResponse<BlogDTO>> updateBlog(@PathVariable Long id,
                                                               @RequestBody BlogRequestDTO requestDTO) {
        return ResponseEntity.ok(blogService.updateBlog(id, requestDTO));
    }

    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<GeneralResponse<String>> deleteBlog(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.deleteBlog(id));
    }
}