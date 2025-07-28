package com.fpt.capstone.tourism.dto.response.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerBookingDetailDTO {
    private Long id;
    private String bookingCode;
    private String tourName;
    private LocalDateTime createdAt;
    private String status;
    private String operator;
    private LocalDateTime departureDate;
    private String tourType;
    private List<String> themes;
    private int durationDays;
    private String departLocation;
    private List<String> destinations;
    private int totalSeats;
    private int soldSeats;
    private int remainingSeats;
    private List<TourScheduleDTO> schedules;
}