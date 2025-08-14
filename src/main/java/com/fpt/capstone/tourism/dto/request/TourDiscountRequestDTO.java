package com.fpt.capstone.tourism.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Data
public class TourDiscountRequestDTO {
    @NotNull
    private Long scheduleId;

    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "100.0")
    private double discountPercent;

    @NotNull
    @Future
    private LocalDateTime startDate;

    @NotNull
    @Future
    private LocalDateTime endDate;
}