package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.repository.tour.FeedbackRepository;
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

    @Override
    public PagingDTO<TourSummaryDTO> getFixedTours(Pageable pageable) {
        // Gọi repository để lấy dữ liệu tour từ database
        Page<Tour> tourPage = tourRepository.findByTourTypeAndTourStatus(
                TourType.FIXED,
                TourStatus.PUBLISHED,
                pageable
        );

        // Chuyển đổi từ Page<Tour> sang List<TourSummaryDTO>
        List<TourSummaryDTO> tourSummaries = tourPage.getContent().stream()
                .map(tour -> {
                    // Map các thông tin cơ bản bằng MapStruct
                    TourSummaryDTO dto = tourMapper.tourToTourSummaryDTO(tour);
                    // Lấy và gán điểm rating trung bình
                    Double avgRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
                    dto.setAverageRating(avgRating);
                    return dto;
                })
                .collect(Collectors.toList());

        // Xây dựng và trả về đối tượng PagingDTO
        return PagingDTO.<TourSummaryDTO>builder()
                .page(tourPage.getNumber())
                .size(tourPage.getSize())
                .total(tourPage.getTotalElements())
                .items(tourSummaries)
                .build();
    }
}
