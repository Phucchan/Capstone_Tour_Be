package com.fpt.capstone.tourism.dto.response.tour;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourScheduleDTO {
    private Long id;
    private LocalDateTime departureDate;
    private LocalDateTime endDate;
    private double price;
    private int availableSeats;
    private double extraHotelCost;
}