package com.fpt.capstone.tourism.dto.common.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourScheduleShortInfoDTO {
    private Long id;
    private LocalDateTime departureDate;
    private LocalDateTime endDate;
}