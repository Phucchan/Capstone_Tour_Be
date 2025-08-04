package com.fpt.capstone.tourism.dto.response;

import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBookingNotificationDTO {
    private Long id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private List<String> destinations;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private String detailUrl;
    private RequestBookingStatus status;
}