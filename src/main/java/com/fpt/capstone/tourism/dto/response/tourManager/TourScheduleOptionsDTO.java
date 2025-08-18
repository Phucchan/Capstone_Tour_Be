package com.fpt.capstone.tourism.dto.response.tourManager;

import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourThemeOptionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourScheduleOptionsDTO {
    private Long tourId;
    private String tourName;
    private String tourType;
    private List<TourThemeOptionDTO> themes;
    private Integer durationDays;
    private LocalDateTime createdDate;
    private UserBasicDTO createdBy;
    private List<UserBasicDTO> coordinators;
    private List<TourPaxManagerDTO> tourPaxes;
}