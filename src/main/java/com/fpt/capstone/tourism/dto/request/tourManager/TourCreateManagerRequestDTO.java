package com.fpt.capstone.tourism.dto.request.tourManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourCreateManagerRequestDTO {
    private String name;
    private String thumbnailUrl;
    private List<Long> tourThemeIds;
    private Long departLocationId;
    private List<Long> destinationLocationIds;
    private Integer durationDays;
    private String description;
}