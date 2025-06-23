package com.fpt.capstone.tourism.dto.response.tourManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailDTO {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private String tourThemeName;
    private String departLocationName;
    private String destinationLocationName;
    private Integer durationDays;
    private String description;
}
