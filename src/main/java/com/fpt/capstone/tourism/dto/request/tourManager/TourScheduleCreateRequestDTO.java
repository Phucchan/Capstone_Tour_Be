package com.fpt.capstone.tourism.dto.request.tourManager;

import com.fpt.capstone.tourism.model.enums.ScheduleRepeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourScheduleCreateRequestDTO {
    private Long coordinatorId;
    private Long tourPaxId;
    private LocalDateTime departureDate;
    private LocalDateTime endDate;
    private ScheduleRepeatType repeatType;
    private Integer repeatCount;
}
