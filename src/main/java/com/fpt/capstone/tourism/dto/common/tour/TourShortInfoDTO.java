package com.fpt.capstone.tourism.dto.common.tour;

import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourShortInfoDTO {
    private Long id;
    private String code;
    private String name;
    private int durationDays;
    private String thumbnailUrl;
    private String tourThemeName;
    private String departLocationName;
    private String tourTransport;
}