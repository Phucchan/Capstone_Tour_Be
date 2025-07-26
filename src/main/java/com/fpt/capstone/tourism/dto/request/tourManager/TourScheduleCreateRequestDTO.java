package com.fpt.capstone.tourism.dto.request.tourManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourScheduleCreateRequestDTO {
    private Long coordinatorId;
    private Long tourPaxId;
    private LocalDateTime departureDate;
}
