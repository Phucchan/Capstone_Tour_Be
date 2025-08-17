package com.fpt.capstone.tourism.dto.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TourDiscountDTO {
    private Long id;
    private Long scheduleId;
    private Long tourId;
    private String tourName;
    private double discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}