package com.fpt.capstone.tourism.dto.response.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerBookingSummaryDTO {
    private Long id;
    private String tourName;
    private LocalDateTime bookingDate;
    private LocalDateTime departureDate;
    private String bookingCode;
    private int seats;
    private String customer;
    private String status;
}
