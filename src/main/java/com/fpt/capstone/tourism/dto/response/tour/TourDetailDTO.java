package com.fpt.capstone.tourism.dto.response.tour;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailDTO {
    private Long id;
    private String name;
    private String description;
    private String thumbnailUrl;
    private int durationDays;
    private String region;
    private String tourThemeName;
    private Double averageRating;
    private List<TourDayDetailDTO> days;
    private List<FeedbackDTO> feedback;
    private List<TourScheduleDTO> schedules;
}