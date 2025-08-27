package com.fpt.capstone.tourism.dto.response;

import com.fpt.capstone.tourism.model.enums.CostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailDTO {
    private Long id;
    private String name;
    private Long partnerId;
    private String partnerName;
    private Long serviceTypeId;
    private String serviceTypeName;
    private String imageUrl;
    private String description;
    private double netPrice;
    private double sellingPrice;
    private CostType costType;
    private String status;
}