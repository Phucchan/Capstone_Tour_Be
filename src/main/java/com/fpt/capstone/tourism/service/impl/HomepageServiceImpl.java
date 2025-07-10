package com.fpt.capstone.tourism.service.impl;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.HomepageDataDTO;
import com.fpt.capstone.tourism.dto.response.homepage.SaleTourDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.blog.Tag;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDiscount;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.repository.tour.*;
import com.fpt.capstone.tourism.service.HomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.fpt.capstone.tourism.dto.common.location.LocationWithoutGeoPositionDTO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.repository.LocationRepository;

@Service
@RequiredArgsConstructor
public class HomepageServiceImpl implements HomepageService {

    // Các repository
    private final FeedbackRepository feedbackRepository;
    private final BlogRepository blogRepository;
    private final LocationRepository locationRepository;
    private final TourPaxRepository tourPaxRepository;
    private final TourRepository tourRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final TourDiscountRepository tourDiscountRepository;

    // Các mapper
    private final BlogMapper blogMapper;
    private final LocationMapper locationMapper;


    @Override
    public HomepageDataDTO getHomepageData() {
        List<LocationWithoutGeoPositionDTO> locations = getHomepageLocations();
        List<TourSummaryDTO> highlyRatedTours = getHighlyRatedTours();
        List<BlogSummaryDTO> recentBlogs = getRecentBlogs();
        List<SaleTourDTO> saleTours = getSaleTours();

        return HomepageDataDTO.builder()
                .locations(locations)
                .highlyRatedTours(highlyRatedTours)
                .saleTours(saleTours)
                .recentBlogs(recentBlogs)
                .build();
    }

    private List<LocationWithoutGeoPositionDTO> getHomepageLocations() {
        List<Location> locations = locationRepository.findRandomLocation(8);
        return locations.stream()
                .map(locationMapper::toLocationWithoutGeoPositionDTO)
                .collect(Collectors.toList());
    }

    private List<TourSummaryDTO> getHighlyRatedTours() {
        List<Tour> topTours = feedbackRepository.findTopRatedTours(PageRequest.of(0, 5));
        return topTours.stream()
                .map(this::mapTourToSummaryDTO)
                .collect(Collectors.toList());
    }
    private List<SaleTourDTO> getSaleTours() {
        List<TourDiscount> discounts = tourDiscountRepository.findTopDiscountedTours(
                LocalDateTime.now(),
                PageRequest.of(0, 4)
        );
        return discounts.stream()
                .map(this::mapDiscountToSaleDTO)
                .collect(Collectors.toList());
    }




    private List<BlogSummaryDTO> getRecentBlogs() {
        // 1. Lấy 5 bài blog mới nhất từ DB
        List<Blog> blogs = blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc();

        // 2. Chuyển đổi từ List<Blog> sang List<BlogSummaryDTO>
        return blogs.stream().map(blog -> {
            // Dùng mapper để chuyển đổi các trường cơ bản (id, title, authorName,...)
            BlogSummaryDTO dto = blogMapper.blogToBlogSummaryDTO(blog);

            // Lấy danh sách Tag entities từ blog, chuyển thành List<String> chứa tên các tag
            List<String> tagNames;
            if (blog.getBlogTags() != null) {
                tagNames = blog.getBlogTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList());
            } else {
                tagNames = Collections.emptyList(); // Trả về danh sách rỗng nếu không có tag
            }

            // Gán danh sách tên tag vào DTO
            dto.setTags(tagNames);

            return dto;
        }).collect(Collectors.toList());
    }

    private SaleTourDTO mapDiscountToSaleDTO(TourDiscount discount) {
        Tour tour = discount.getTour();

        Double averageRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
        Double startingPrice = tourPaxRepository.findStartingPriceByTourId(tour.getId());

        List<TourSchedule> futureSchedules = tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(
                tour.getId(),
                LocalDateTime.now()
        );

        List<LocalDateTime> departureDates = futureSchedules.stream()
                .map(TourSchedule::getDepartureDate)
                .collect(Collectors.toList());

        return SaleTourDTO.builder()
                .id(tour.getId())
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
                .build();
    }
    /**
     * PHƯƠNG THỨC HELPER MỚI
     * Tách logic chuyển đổi từ Tour -> TourSummaryDTO ra một nơi chung để tái sử dụng.
     * @param tour Entity Tour cần chuyển đổi.
     * @return DTO TourSummaryDTO đã có đủ thông tin.
     */
    private TourSummaryDTO mapTourToSummaryDTO(Tour tour) {
        // Lấy các thông tin phụ
        Double averageRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
        Double startingPrice = tourPaxRepository.findStartingPriceByTourId(tour.getId());

        // Lấy lịch trình gần nhất
        List<TourSchedule> futureSchedules  = tourScheduleRepository.findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(
                tour.getId(),
                LocalDateTime.now()
        );

        List<LocalDateTime> departureDates = futureSchedules.stream()
                .map(TourSchedule::getDepartureDate)
                .collect(Collectors.toList());

        // Xây dựng DTO
        return TourSummaryDTO.builder()
                .id(tour.getId())
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
                .build();

    }
}
