package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.repository.tour.*;
import com.fpt.capstone.tourism.service.TourService;
import com.fpt.capstone.tourism.specifications.TourSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.TourDetailMapper;
import com.fpt.capstone.tourism.model.tour.Feedback;
import com.fpt.capstone.tourism.model.tour.TourDay;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final FeedbackRepository feedbackRepository;
    private final TourDayRepository tourDayRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final TourPaxRepository tourPaxRepository;
    private final BookingRepository bookingRepository;
    private final TourMapper tourMapper;
    private final TourDetailMapper tourDetailMapper;

    @Override
    public PagingDTO<TourSummaryDTO> searchTours(Double priceMin, Double priceMax, Long departId, Long destId, LocalDate date, Pageable pageable) {
        // 1. Kết hợp các Specification lại với nhau
        Specification<Tour> spec = Specification
                .where(TourSpecification.hasPriceInRange(priceMin, priceMax))
                .and(TourSpecification.hasDepartureLocation(departId))
                .and(TourSpecification.hasDestination(destId))
                .and(TourSpecification.hasDepartureDate(date));

        // 2. Gọi phương thức findAll mới của repository với Specification
        Page<Tour> tourPage = tourRepository.findAll(spec, pageable);

        // 3. Tái sử dụng phương thức map DTO đã có và trả về kết quả
        return mapTourPageToPagingDTO(tourPage);
    }
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
        List<TourSchedule> schedules = tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(tourId, LocalDateTime.now());
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
        List<TourScheduleDTO> scheduleDTOs = schedules.stream().map(schedule -> {
            // Map các trường cơ bản từ schedule -> scheduleDTO
            TourScheduleDTO dto = tourDetailMapper.tourScheduleToTourScheduleDTO(schedule);

            // Tính toán số chỗ trống
            int totalSlots = schedule.getTourPax().getMaxQuantity();
            int bookedSlots = bookingRepository.sumGuestsByTourScheduleId(schedule.getId());
            int availableSeats = totalSlots - bookedSlots;

            // Gán số chỗ trống vào DTO
            dto.setAvailableSeats(availableSeats);

            return dto;
        }).collect(Collectors.toList());

        tourDetailDTO.setSchedules(scheduleDTOs);

        return tourDetailDTO;
    }


    // Phương thức helper để map danh sách tour tóm tắt
    private PagingDTO<TourSummaryDTO> mapTourPageToPagingDTO(Page<Tour> tourPage) {
        List<TourSummaryDTO> tourSummaries = tourPage.getContent().stream()
                .map(tour -> {
                    // Lấy các thông tin phụ
                    Double avgRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
                    Double startingPrice = tourPaxRepository.findStartingPriceByTourId(tour.getId());

                    // Lấy danh sách các lịch trình trong tương lai
                    List<TourSchedule> futureSchedules = tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(
                            tour.getId(),
                            LocalDateTime.now()
                    );
                    List<LocalDateTime> departureDates = futureSchedules.stream()
                            .map(TourSchedule::getDepartureDate)
                            .collect(Collectors.toList());

                    // Xây dựng DTO với các trường đã được cập nhật
                    return TourSummaryDTO.builder()
                            .id(tour.getId())
                            .name(tour.getName())
                            .thumbnailUrl(tour.getThumbnailUrl())
                            .durationDays(tour.getDurationDays())
                            .region(tour.getRegion() != null ? tour.getRegion().name() : null)
                            .locationName(tour.getDepartLocation() != null ? tour.getDepartLocation().getName() : null)
                            .averageRating(avgRating)
                            .startingPrice(startingPrice)
                            .code(tour.getCode())
                            .tourTransport(tour.getTourTransport() != null ? tour.getTourTransport().name() : null)
                            .departureDates(departureDates)
                            .build();
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
