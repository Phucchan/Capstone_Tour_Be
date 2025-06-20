package com.fpt.capstone.tourism.service.impl;
import com.fpt.capstone.tourism.dto.response.homepage.BlogSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.HomepageDataDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourThemeDTO;
import com.fpt.capstone.tourism.mapper.BlogMapper;
import com.fpt.capstone.tourism.mapper.TourThemeMapper;
import com.fpt.capstone.tourism.model.blog.Blog;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourTheme;
import com.fpt.capstone.tourism.repository.blog.BlogRepository;
import com.fpt.capstone.tourism.repository.tour.FeedbackRepository;
import com.fpt.capstone.tourism.repository.tour.TourThemeRepository;
import com.fpt.capstone.tourism.service.HomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomepageServiceImpl implements HomepageService {

    private final TourThemeRepository tourThemeRepository;
    private final FeedbackRepository feedbackRepository;
    private final BlogRepository blogRepository;

    private final TourThemeMapper tourThemeMapper;
    private final BlogMapper blogMapper;

    @Override
    public HomepageDataDTO getHomepageData() {
        List<TourThemeDTO> themes = getHomepageThemes();
        List<TourSummaryDTO> highlyRatedTours = getHighlyRatedTours();
        List<BlogSummaryDTO> recentBlogs = getRecentBlogs();

        return HomepageDataDTO.builder()
                .themes(themes)
                .highlyRatedTours(highlyRatedTours)
                .recentBlogs(recentBlogs)
                .build();
    }

    /**
     * Fetches a list of tour themes for the homepage.
     * Tạm thời trả về danh sách rỗng. Sẽ hoàn thiện ở bước sau.
     */
    private List<TourThemeDTO> getHomepageThemes() {
        List<TourTheme> themes = tourThemeRepository.findTop6ByDeletedFalse();
        return themes.stream()
                .map(tourThemeMapper::tourThemeToTourThemeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a list of top-rated tours for the homepage.
     * Tạm thời trả về danh sách rỗng. Sẽ hoàn thiện ở bước sau.
     */
    private List<TourSummaryDTO> getHighlyRatedTours() {
        List<Tour> topTours = feedbackRepository.findTopRatedTours(PageRequest.of(0, 5));

        return topTours.stream().map(tour -> {
            Double averageRating = feedbackRepository.findAverageRatingByTourId(tour.getId());
            return TourSummaryDTO.builder()
                    .id(tour.getId())
                    .name(tour.getName())
                    .thumbnailUrl(tour.getThumbnailUrl())
                    .averageRating(averageRating)
                    .durationDays(tour.getDurationDays())
                    .region(tour.getRegion().name())
                    .locationName(tour.getDepartLocation().getName())
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