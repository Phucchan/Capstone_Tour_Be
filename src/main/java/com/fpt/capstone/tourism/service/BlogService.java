package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.BlogManagerDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.BlogManagerRequestDTO;

import java.util.List;

public interface BlogService {
    GeneralResponse<BlogManagerDTO> createBlog(BlogManagerRequestDTO requestDTO);
    GeneralResponse<BlogManagerDTO> updateBlog(Long id, BlogManagerRequestDTO requestDTO);
    GeneralResponse<String> deleteBlog(Long id);
    GeneralResponse<List<BlogManagerDTO>> getBlogs();
}