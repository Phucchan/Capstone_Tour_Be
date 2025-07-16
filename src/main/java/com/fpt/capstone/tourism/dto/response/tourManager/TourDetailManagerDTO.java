package com.fpt.capstone.tourism.dto.response.tourManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailManagerDTO {
    private Long id;
    private String code;
    private String name;
    private String thumbnailUrl;
    private List<String> tourThemeNames;
    private String departLocationName;
    private List<String> destinationLocationNames;
    private Integer durationDays;
    private String description;
}
