package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;

import java.util.List;

public interface WishlistService {
    GeneralResponse<String> addToWishlist(Long userId, Long tourId);
    GeneralResponse<String> deleteWishlist(Long wishlistId, Long userId);
    GeneralResponse<List<TourSummaryDTO>> getWishlist(Long userId);
}