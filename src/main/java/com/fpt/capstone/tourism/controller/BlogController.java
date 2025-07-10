package com.fpt.capstone.tourism.controller;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.BlogDetailDTO;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/blogs") // Endpoint chung cho blog
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<GeneralResponse<PagingDTO<BlogSummaryDTO>>> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size, // Mặc định là 8 blog/trang
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        PagingDTO<BlogSummaryDTO> result = blogService.getAllBlogs(pageable);
        return ResponseEntity.ok(GeneralResponse.of(result, "Blogs loaded successfully."));
    }

    @GetMapping("/{id}")
    //postman http://localhost:8080/v1/public/blogs/2
    public ResponseEntity<GeneralResponse<BlogDetailDTO>> getBlogDetail(@PathVariable Long id) {
        BlogDetailDTO result = blogService.getBlogDetailById(id);
        return ResponseEntity.ok(GeneralResponse.of(result, "Blog detail loaded successfully."));
    }
}
