package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.BlogManagerDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.BlogManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.BlogDetailManagerDTO;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {
    GeneralResponse<BlogManagerDTO> createBlog(BlogManagerRequestDTO requestDTO);
    GeneralResponse<BlogManagerDTO> updateBlog(Long id, BlogManagerRequestDTO requestDTO);
    GeneralResponse<String> deleteBlog(Long id);
    GeneralResponse<PagingDTO<BlogManagerDTO>> getBlogs(int page, int size);
    GeneralResponse<BlogDetailManagerDTO> getBlog(Long id);

    /**
     * PHƯƠNG THỨC MỚI
     * Lấy danh sách tất cả các bài blog đã xuất bản, có phân trang.
     * @param pageable thông tin phân trang.
     * @return PagingDTO chứa danh sách BlogSummaryDTO.
     */
    PagingDTO<BlogSummaryDTO> getAllBlogs(Pageable pageable);

}