package com.fpt.capstone.tourism.dto.response;

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
public class RequestBookingNotificationDTO {
    private Long id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private String detailUrl;
}