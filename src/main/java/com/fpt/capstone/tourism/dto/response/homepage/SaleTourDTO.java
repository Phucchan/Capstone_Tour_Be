package com.fpt.capstone.tourism.dto.response.homepage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing a tour currently on sale.
 * Contains basic tour information along with the discount percentage.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleTourDTO {
    private Long scheduleId;
    private String name;
    private String thumbnailUrl;
    private Double averageRating;
    private int durationDays;
    private String region;
    private String locationName;
    private Double startingPrice;
    private String code;
    private String tourTransport;
    private List<LocalDateTime> departureDates;
    private double discountPercent;
    private int availableSeats;
}