package com.fpt.capstone.tourism.dto.response.tourManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourCostSummaryDTO {
    private double totalFixedCost;      // Tổng chi phí cố định
    private double totalPerPersonCost;  // Tổng chi phí trên mỗi khách
}