package com.fpt.capstone.tourism.dto.request.tourManager;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourPaxUpdateRequestDTO {
    private Integer minQuantity;
    private Integer maxQuantity;
    private Double fixedPrice;
    private Double extraHotelCost;
    private Double sellingPrice;
}

