package com.fpt.capstone.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSummaryDTO {
    private Long id;
    private Long tourId;
    private String bookingCode;
    private String tourName;
    private String status;
    private double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime departureDate;
    private boolean hasRefundInfo;
}