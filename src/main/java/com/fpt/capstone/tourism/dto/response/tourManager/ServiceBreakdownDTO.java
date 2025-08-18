package com.fpt.capstone.tourism.dto.response.tourManager;

import com.fpt.capstone.tourism.model.enums.CostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBreakdownDTO {
    private Long dayId;
    private Long serviceId;
    private int dayNumber;
    private String serviceTypeName;
    private String partnerName;
    private String partnerAddress;
    private double nettPrice;
    private double sellingPrice;
    private CostType costType;
}