package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import org.springframework.data.domain.Pageable;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;

import java.time.LocalDate;

public interface TourService {
    /**
     * Retrieves a paginated list of fixed, published tours.
     * @param pageable Pagination information.
     * @return A PagingDTO containing a list of TourSummaryDTO.
     */
    PagingDTO<TourSummaryDTO> getFixedTours(Pageable pageable);

    PagingDTO<TourSummaryDTO> getToursByLocation(Long locationId, Pageable pageable);

    TourDetailDTO getTourDetailById(Long tourId);

    PagingDTO<TourSummaryDTO> searchTours(Double priceMin, Double priceMax, Long departId, Long destId, LocalDate date, Pageable pageable);
}
