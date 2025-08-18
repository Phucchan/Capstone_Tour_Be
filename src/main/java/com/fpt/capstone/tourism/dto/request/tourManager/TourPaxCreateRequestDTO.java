package com.fpt.capstone.tourism.dto.request.tourManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourPaxCreateRequestDTO {
    private int minQuantity;
    private int maxQuantity;
    private Double fixedPrice;
    private Double extraHotelCost;
    private Double sellingPrice;
    private boolean manualPrice;
}