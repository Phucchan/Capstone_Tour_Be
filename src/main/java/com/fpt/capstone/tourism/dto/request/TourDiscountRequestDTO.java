package com.fpt.capstone.tourism.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TourDiscountRequestDTO {
    private Long scheduleId;
    private double discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}