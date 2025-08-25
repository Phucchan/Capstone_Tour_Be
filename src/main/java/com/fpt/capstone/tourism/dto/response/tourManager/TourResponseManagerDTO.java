package com.fpt.capstone.tourism.dto.response.tourManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseManagerDTO {
    private Long id;
    private String code;
    private String name;
    private String thumbnailImage;
    private String typeName;
    private String tourTransport;
    private String tourStatus;
    private Integer durationDays;
    private LocalDateTime createdAt;
    private String createdByName;
}
