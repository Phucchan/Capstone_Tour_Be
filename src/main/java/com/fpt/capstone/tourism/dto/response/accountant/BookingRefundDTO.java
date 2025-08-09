package com.fpt.capstone.tourism.dto.response.accountant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRefundDTO {
    private Long bookingId;
    private String tourCode;
    private String tourName;
    private String tourType;
    private LocalDateTime startDate;
    private String status;
    private String customerName;
}