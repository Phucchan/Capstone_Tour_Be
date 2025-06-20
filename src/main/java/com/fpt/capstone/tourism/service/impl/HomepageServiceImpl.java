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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomepageServiceImpl implements HomepageService {


    private final FeedbackRepository feedbackRepository;
    private final BlogRepository blogRepository;


    private final BlogMapper blogMapper;

    @Override
    public HomepageDataDTO getHomepageData() {
        List<TourSummaryDTO> highlyRatedTours = getHighlyRatedTours();
        List<BlogSummaryDTO> recentBlogs = getRecentBlogs();

        return HomepageDataDTO.builder()
                .highlyRatedTours(highlyRatedTours)
                .recentBlogs(recentBlogs)
                .build();
    }


    /**
     * Fetches a list of top-rated tours for the homepage.
     * Tạm thời trả về danh sách rỗng. Sẽ hoàn thiện ở bước sau.
     */
    private List<TourSummaryDTO> getHighlyRatedTours() {
        //láy 5 tour có đánh giá cao nhất từ repository
        List<Tour> topTours = feedbackRepository.findTopRatedTours(PageRequest.of(0, 5));

        // Chuyển đổi sang DTO và gán điểm rating trung bình
        return topTours.stream().map(tour -> {
            Double averageRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
            return TourSummaryDTO.builder()
                    .id(tour.getId())
                    .name(tour.getName())
                    .thumbnailUrl(tour.getThumbnailUrl())
                    .averageRating(averageRating)
                    .durationDays(tour.getDurationDays())
                    .region(tour.getRegion() != null ? tour.getRegion().name() : null)
                    .locationName(tour.getDepartLocation() != null ? tour.getDepartLocation().getName() : null)
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