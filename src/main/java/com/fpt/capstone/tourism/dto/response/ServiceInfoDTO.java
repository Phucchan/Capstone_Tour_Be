package com.fpt.capstone.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfoDTO {
    private Long id;
    private String name;
    private String partnerName;
    private double sellingPrice;
    private String serviceTypeName;
    private String status;
}