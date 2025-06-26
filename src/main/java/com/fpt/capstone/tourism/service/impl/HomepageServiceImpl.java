package com.fpt.capstone.tourism.service.impl;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.HomepageDataDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.blog.Tag;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.repository.tour.FeedbackRepository;
import com.fpt.capstone.tourism.repository.tour.TourRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
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
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;

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

    // Các mapper
    private final BlogMapper blogMapper;
    private final LocationMapper locationMapper;


    @Override
    public HomepageDataDTO getHomepageData() {
        List<LocationWithoutGeoPositionDTO> locations = getHomepageLocations();
        List<TourSummaryDTO> highlyRatedTours = getHighlyRatedTours();
        List<BlogSummaryDTO> recentBlogs = getRecentBlogs();

        return HomepageDataDTO.builder()
                .locations(locations)
                .highlyRatedTours(highlyRatedTours)
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
        Optional<TourSchedule> nextScheduleOpt = tourScheduleRepository.findFirstByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(
                tour.getId(),
                LocalDateTime.now()
        );
        LocalDateTime nextDepartureDate = nextScheduleOpt.map(TourSchedule::getDepartureDate).orElse(null);

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
                .nextDepartureDate(nextDepartureDate)
                .build();
    }
}
