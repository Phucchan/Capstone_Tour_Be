package com.fpt.capstone.tourism.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TourDiscountSummaryDTO {
    private Long id;
    private Long scheduleId;
    private String tourName;
    private double discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}