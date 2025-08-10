package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.SaleTourDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import org.springframework.data.domain.Pageable;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;

import java.time.LocalDate;

public interface TourService {

    TourDetailDTO getTourDetailById(Long tourId);

    PagingDTO<TourSummaryDTO> filterTours(Double priceMin, Double priceMax, Long departId, Long destId, LocalDate date, Pageable pageable);

    PagingDTO<SaleTourDTO> getDiscountTours(Pageable pageable);

    PagingDTO<TourSummaryDTO> getCustomToursByUser(Long userId, String search, Pageable pageable);
}
