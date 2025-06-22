package com.fpt.capstone.tourism.service.impl;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.HomepageDataDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.repository.tour.FeedbackRepository;
import com.fpt.capstone.tourism.service.HomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.fpt.capstone.tourism.dto.common.location.LocationWithoutGeoPositionDTO;


import java.util.List;
import java.util.stream.Collectors;
import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;

@Service
@RequiredArgsConstructor
public class HomepageServiceImpl implements HomepageService {


    private final FeedbackRepository feedbackRepository;
    private final BlogRepository blogRepository;
    private final LocationRepository locationRepository;
    private final TourPaxRepository tourPaxRepository;
    private final LocationMapper locationMapper;

    private final BlogMapper blogMapper;

    @Override
    public HomepageDataDTO getHomepageData() {
        List<LocationWithoutGeoPositionDTO> locations = getHomepageLocations(); // Sửa kiểu
        List<TourSummaryDTO> highlyRatedTours = getHighlyRatedTours();
        List<BlogSummaryDTO> recentBlogs = getRecentBlogs();

        return HomepageDataDTO.builder()
                .locations(locations) // Mới
                .highlyRatedTours(highlyRatedTours)
                .recentBlogs(recentBlogs)
                .build();
    }

    private List<LocationWithoutGeoPositionDTO> getHomepageLocations() {
        List<Location> locations = locationRepository.findRandomLocation(6);
        // Sử dụng đúng phương thức map toLocationWithoutGeoPositionDTO
        return locations.stream()
                .map(locationMapper::toLocationWithoutGeoPositionDTO)
                .collect(Collectors.toList());
    }

    private List<TourSummaryDTO> getHighlyRatedTours() {
        List<Tour> topTours = feedbackRepository.findTopRatedTours(PageRequest.of(0, 5));

        return topTours.stream().map(tour -> {
            Double averageRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
            Double startingPrice = tourPaxRepository.findStartingPriceByTourId(tour.getId()); // Mới

            return TourSummaryDTO.builder()
                    .id(tour.getId())
                    .name(tour.getName())
                    .thumbnailUrl(tour.getThumbnailUrl())
                    .averageRating(averageRating)
                    .durationDays(tour.getDurationDays())
                    .region(tour.getRegion() != null ? tour.getRegion().name() : null)
                    .locationName(tour.getDepartLocation() != null ? tour.getDepartLocation().getName() : null)
                    .startingPrice(startingPrice) // Mới
                    .build();
        }).collect(Collectors.toList());
    }
    
    /**
     * Fetches a list of recent blog posts for the homepage.
     * Tạm thời trả về danh sách rỗng. Sẽ hoàn thiện ở bước sau.
     */
    private List<BlogSummaryDTO> getRecentBlogs() {
        List<Blog> blogs = blogRepository.findFirst5ByDeletedFalseOrderByCreatedAtDesc();
        return blogs.stream()
                .map(blogMapper::blogToBlogSummaryDTO)
                .collect(Collectors.toList());
    }
}