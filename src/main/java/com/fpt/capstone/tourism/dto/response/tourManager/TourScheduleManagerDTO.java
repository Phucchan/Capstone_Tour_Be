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
public class TourScheduleManagerDTO {
    private Long id;
    private Long coordinatorId;
    private Long tourPaxId;
    private LocalDateTime departureDate;
    private LocalDateTime endDate;
    private Double price;
    private Long discountId;
    private Double discountPercent;
}