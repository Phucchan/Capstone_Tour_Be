package com.fpt.capstone.tourism.dto.request;

import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBookingSummaryDTO {
    private Long id;
    private String customerName;
    private String customerPhone;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private RequestBookingStatus status;
    private LocalDateTime createdAt;
}