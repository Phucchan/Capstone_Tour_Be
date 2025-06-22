package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.BlogDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.BlogRequestDTO;

import java.util.List;

public interface BlogService {
    GeneralResponse<BlogDTO> createBlog(BlogRequestDTO requestDTO);
    GeneralResponse<BlogDTO> updateBlog(Long id, BlogRequestDTO requestDTO);
    GeneralResponse<String> deleteBlog(Long id);
    GeneralResponse<List<BlogDTO>> getBlogs();
}