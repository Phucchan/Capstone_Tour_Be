package com.fpt.capstone.tourism.dto.request;

import com.fpt.capstone.tourism.model.enums.CostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreateRequestDTO {
    private String name;
    private Long serviceTypeId;
    private Long partnerId;
    private String imageUrl;
    private String description;
    private double nettPrice;
    private double sellingPrice;
    private CostType costType;
}
