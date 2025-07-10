package com.fpt.capstone.tourism.dto.request.tourManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourUpdateManagerRequestDTO {
    private String code;
    private String name;
    private String thumbnailUrl;
    private Long tourThemeId;
    private Long departLocationId;
    private Long destinationLocationId;
    private Integer durationDays;
    private String description;
}