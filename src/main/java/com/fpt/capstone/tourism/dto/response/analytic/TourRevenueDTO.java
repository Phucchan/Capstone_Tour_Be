package com.fpt.capstone.tourism.dto.response.analytic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourRevenueDTO {
    private Long id;
    private String name;
    private String tourType;
    private Double totalRevenue;
}