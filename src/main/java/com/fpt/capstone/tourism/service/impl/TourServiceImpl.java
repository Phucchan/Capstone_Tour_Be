package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.SaleTourDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourMarkupResponseDTO;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.*;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.tour.*;
import com.fpt.capstone.tourism.service.TourService;
import com.fpt.capstone.tourism.specifications.TourSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.TourDetailMapper;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import static com.fpt.capstone.tourism.constants.Constants.Message.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final FeedbackRepository feedbackRepository;
    private final TourDayRepository tourDayRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final TourPaxRepository tourPaxRepository;
    private final BookingRepository bookingRepository;
    private final TourDiscountRepository tourDiscountRepository;
    private final TourMapper tourMapper;
    private final TourDetailMapper tourDetailMapper;

    @Override
    public PagingDTO<TourSummaryDTO> filterTours(Double priceMin, Double priceMax, Long departId, Long destId, LocalDate date, String name, Pageable pageable) {

        // 1. Bắt đầu với một Specification cơ sở (luôn lọc các tour đã publish)
        // KHÔNG DÙNG .where() nữa
        Specification<Tour> spec = TourSpecification.isPublished();

        // 2. Tuần tự thêm các điều kiện lọc nếu tham số của chúng tồn tại
        if (priceMin != null || priceMax != null) {
            spec = spec.and(TourSpecification.hasPriceInRange(priceMin, priceMax));
        }
        if (departId != null) {
            spec = spec.and(TourSpecification.hasDepartureLocation(departId));
        }
        if (destId != null) {
            spec = spec.and(TourSpecification.hasDestination(destId));
        }
        if (date != null) {
            spec = spec.and(TourSpecification.hasDepartureDate(date));
        }
        if (name != null && !name.isBlank()) {
            spec = spec.and(TourSpecification.hasNameLike(name));
        }

        // 3. Gọi repository với specification đã được xây dựng hoàn chỉnh
        Page<Tour> tourPage = tourRepository.findAll(spec, pageable);

        // 4. Map kết quả sang DTO và trả về
        return mapTourPageToPagingDTO(tourPage);
    }


    @Override
    public PagingDTO<SaleTourDTO> getDiscountTours(Pageable pageable) {
        Page<TourDiscount> discountPage = tourDiscountRepository.findActiveDiscountedTours(LocalDateTime.now(), pageable);
        List<SaleTourDTO> saleTours = discountPage.getContent().stream()
                .map(this::mapDiscountToSaleDTO)
                .collect(Collectors.toList());

        return PagingDTO.<SaleTourDTO>builder()
                .page(discountPage.getNumber())
                .size(discountPage.getSize())
                .total(discountPage.getTotalElements())
                .items(saleTours)
                .build();
    }

    @Transactional
    @Override
    public TourDetailDTO getTourDetailById(Long tourId) {
        log.info("Fetching tour details for tour ID: {}", tourId);
        // 1. Lấy entity Tour chính
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found" ));
        log.info("Finished tour details for tour ID: {}", tourId);

        // 2. Lấy các thông tin liên quan
        log.info("Fetching tour days for tour ID: {}", tourId);
        List<TourDay> tourDays = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);
        log.info("Finished tour details for tour ID: {}", tourId);

        log.info("Fetching feedbacks for tour ID: {}", tourId);
        List<Feedback> feedbackList = feedbackRepository.findByBooking_TourSchedule_Tour_Id(tourId);
        log.info("Finished feedbacks for tour ID: {}", tourId);

        log.info("Fetching schedules for tour ID: {}", tourId);
        List<TourSchedule> schedules = tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(tourId, LocalDateTime.now());
        log.info("Finished schedules for tour ID: {}", tourId);

        log.info("Fetching Average Rating for tour ID: {}", tourId);
        Double averageRating = feedbackRepository.findAverageRatingByTourId(tourId);
        log.info("Finished Average Rating for tour ID: {}", tourId);

        log.info("Mapping tour entity to dto: {}", tourId);
        // 3. Sử dụng mapper để chuyển đổi Tour -> TourDetailDTO
        TourDetailDTO tourDetailDTO = tourDetailMapper.tourToTourDetailDTO(tour);
        tourDetailDTO.setAverageRating(averageRating);
        log.info("Finished tour entity to dto: {}", tourId);

        // 4. Map các danh sách con
        log.info("Mapping tour days information to dto: {}", tourId);
        tourDetailDTO.setDays(
                tourDays.stream().map(tourDetailMapper::tourDayToTourDayDetailDTO).collect(Collectors.toList())
        );
        log.info("[TourServiceImpl - line 103] Finished tour days information to dto: {}", tourId);

        log.info("[TourServiceImpl - line 105] Mapping feedback information to dto: {}", tourId);
        tourDetailDTO.setFeedback(
                feedbackList.stream().map(tourDetailMapper::feedbackToFeedbackDTO).collect(Collectors.toList())
        );
        log.info("[TourServiceImpl - line 105] Finished feedback information to dto: {}", tourId);

        log.info("[TourServiceImpl - line 111] Mapping tour schedule information to dto: {}", tourId);
        List<TourScheduleDTO> scheduleDTOs = schedules.stream().map(schedule -> {
            // Map các trường cơ bản từ schedule -> scheduleDTO
            TourScheduleDTO dto = tourDetailMapper.tourScheduleToTourScheduleDTO(schedule);

            // Tính toán số chỗ trống
            int totalSlots = schedule.getTourPax().getMaxQuantity();
            int bookedSlots = bookingRepository.sumGuestsByTourScheduleId(schedule.getId());
            int availableSeats = Math.max(totalSlots - bookedSlots, 0);

            // Gán số chỗ trống vào DTO
            dto.setAvailableSeats(availableSeats);

            return dto;
        }).collect(Collectors.toList());
        log.info("[TourServiceImpl - line 126] Finished mapping tour schedule information to dto: {}", tourId);

        tourDetailDTO.setSchedules(scheduleDTOs);

        return tourDetailDTO;
    }

    @Override
    public PagingDTO<TourSummaryDTO> getCustomToursByUser(Long userId, String search, Pageable pageable) {
        Page<Tour> tourPage;
        if (search != null && !search.isBlank()) {
            tourPage = tourRepository.searchCustomToursByUser(userId, TourType.CUSTOM, search.trim().toLowerCase(), pageable);
        } else {
            tourPage = tourRepository.findByRequestBooking_User_IdAndTourType(userId, TourType.CUSTOM, pageable);
        }
        return mapTourPageToPagingDTO(tourPage);
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
    private SaleTourDTO mapDiscountToSaleDTO(TourDiscount discount) {
        TourSchedule schedule = discount.getTourSchedule();
        Tour tour = schedule.getTour();
        Double averageRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
        Double startingPrice = schedule.getTourPax() != null ? schedule.getTourPax().getSellingPrice() : null;

        List<LocalDateTime> departureDates = List.of(schedule.getDepartureDate());
        int totalSlots = schedule.getTourPax().getMaxQuantity();
        int bookedSlots = bookingRepository.sumGuestsByTourScheduleId(schedule.getId());
        int availableSeats = Math.max(totalSlots - bookedSlots, 0);
        return SaleTourDTO.builder()
                .scheduleId(schedule.getId())
                .tourId(tour.getId())
                .name(tour.getName())
                .thumbnailUrl(tour.getThumbnailUrl())
                .durationDays(tour.getDurationDays())
                .region(tour.getRegion() != null ? tour.getRegion().name() : null)
                .locationName(tour.getDepartLocation() != null ? tour.getDepartLocation().getName() : null)
                .averageRating(averageRating)
                .startingPrice(startingPrice)
                .code(tour.getCode())
                .tourTransport(tour.getTourTransport() != null ? tour.getTourTransport().name() : null)
                .departureDates(departureDates)
                .discountPercent(discount.getDiscountPercent())
                .availableSeats(availableSeats)
                .build();
    }
}
