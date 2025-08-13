package com.fpt.capstone.tourism.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingServiceUpdateDTO {
    private Long partnerId;
    private String imageUrl;
    private String description;
    private Double nettPrice;
    private Double sellingPrice;
    private String costType;
    private String newStatus;
}