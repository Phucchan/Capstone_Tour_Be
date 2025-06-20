package com.fpt.capstone.tourism.controller.Marketing;

import com.fpt.capstone.tourism.dto.common.BlogDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.BlogRequestDTO;
import com.fpt.capstone.tourism.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/blogs")
public class BlogManagementController {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<GeneralResponse<BlogDTO>> createBlog(@RequestBody BlogRequestDTO requestDTO) {
        return ResponseEntity.ok(blogService.createBlog(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<BlogDTO>> updateBlog(@PathVariable Long id,
                                                               @RequestBody BlogRequestDTO requestDTO) {
        return ResponseEntity.ok(blogService.updateBlog(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<String>> deleteBlog(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.deleteBlog(id));
    }
}