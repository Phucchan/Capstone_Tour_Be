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
public class TourDayDetailDTO {
    private Long id;
    private int dayNumber;
    private String title;
    private String description;
    private String locationName;
    private List<ServiceSummaryDTO> services;
}