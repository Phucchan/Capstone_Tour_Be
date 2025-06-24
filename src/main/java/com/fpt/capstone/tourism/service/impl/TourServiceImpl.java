package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.repository.tour.*;
import com.fpt.capstone.tourism.service.TourService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.TourDetailMapper;
import com.fpt.capstone.tourism.model.tour.Feedback;
import com.fpt.capstone.tourism.model.tour.TourDay;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final FeedbackRepository feedbackRepository;
    private final TourDayRepository tourDayRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final TourMapper tourMapper;
    private final TourPaxRepository tourPaxRepository;
    private final TourDetailMapper tourDetailMapper;


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

    @Transactional
    @Override
    public TourDetailDTO getTourDetailById(Long tourId) {
        // 1. Lấy entity Tour chính
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found" ));

        // 2. Lấy các thông tin liên quan
        List<TourDay> tourDays = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);
        List<Feedback> feedbackList = feedbackRepository.findByBooking_TourSchedule_Tour_Id(tourId);
        List<TourSchedule> schedules = tourScheduleRepository.findByTourIdAndDepartureDateAfter(tourId, LocalDateTime.now());
        Double averageRating = feedbackRepository.findAverageRatingByTourId(tourId);

        // 3. Sử dụng mapper để chuyển đổi Tour -> TourDetailDTO
        TourDetailDTO tourDetailDTO = tourDetailMapper.tourToTourDetailDTO(tour);
        tourDetailDTO.setAverageRating(averageRating);

        // 4. Map các danh sách con
        tourDetailDTO.setDays(
                tourDays.stream().map(tourDetailMapper::tourDayToTourDayDetailDTO).collect(Collectors.toList())
        );
        tourDetailDTO.setFeedback(
                feedbackList.stream().map(tourDetailMapper::feedbackToFeedbackDTO).collect(Collectors.toList())
        );
        tourDetailDTO.setSchedules(
                schedules.stream().map(tourDetailMapper::tourScheduleToTourScheduleDTO).collect(Collectors.toList())
        );

        return tourDetailDTO;
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
