package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.repository.tour.FeedbackRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourRepository;
import com.fpt.capstone.tourism.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final FeedbackRepository feedbackRepository;
    private final TourMapper tourMapper;
    private final TourPaxRepository tourPaxRepository;


    @Override
    public PagingDTO<TourSummaryDTO> getFixedTours(Pageable pageable) {
        Page<Tour> tourPage = tourRepository.findByTourTypeAndTourStatus(
                TourType.FIXED,
                TourStatus.PUBLISHED,
                pageable
        );
        return mapTourPageToPagingDTO(tourPage);
    }

    @Override
    public PagingDTO<TourSummaryDTO> getToursByLocation(Long locationId, Pageable pageable) {
        Page<Tour> tourPage = tourRepository.findByDepartLocationIdAndTourStatus(
                locationId,
                TourStatus.PUBLISHED,
                pageable
        );
        return mapTourPageToPagingDTO(tourPage);
    }

    private PagingDTO<TourSummaryDTO> mapTourPageToPagingDTO(Page<Tour> tourPage) {
        List<TourSummaryDTO> tourSummaries = tourPage.getContent().stream()
                .map(tour -> {
                    TourSummaryDTO dto = tourMapper.tourToTourSummaryDTO(tour);
                    Double avgRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
                    Double startingPrice = tourPaxRepository.findStartingPriceByTourId(tour.getId());
                    dto.setAverageRating(avgRating);
                    dto.setStartingPrice(startingPrice);
                    return dto;
                })
                .collect(Collectors.toList());

        return PagingDTO.<TourSummaryDTO>builder()
                .page(tourPage.getNumber())
                .size(tourPage.getSize())
                .total(tourPage.getTotalElements())
                .items(tourSummaries)
                .build();
    }
}
