package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.TourDiscountDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.TourDiscountRequestDTO;
import com.fpt.capstone.tourism.dto.response.TourDiscountSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourResponseManagerDTO;
import jakarta.validation.Valid;

public interface TourDiscountService {
    GeneralResponse<TourDiscountDTO> createDiscount(TourDiscountRequestDTO requestDTO);

    GeneralResponse<PagingDTO<TourDiscountSummaryDTO>> getDiscounts(String keyword, int page, int size);

    GeneralResponse<PagingDTO<TourResponseManagerDTO>> getToursForDiscount(String keyword,
                                                                           int page,
                                                                           int size,
                                                                           Boolean hasDiscount
                                                                           );

    GeneralResponse<Void> deleteDiscount(Long id);

    GeneralResponse<TourDiscountDTO> updateDiscount(Long id, @Valid TourDiscountRequestDTO dto);

    GeneralResponse<TourDiscountDTO> getDiscountById(Long id);
}