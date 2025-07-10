package com.fpt.capstone.tourism.dto.response.tourManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailManagerDTO {
    private String code;
    private Long id;
    private String name;
    private String thumbnailUrl;
    private String tourThemeName;
    private String departLocationName;
    private Integer durationDays;
    private String description;
}
