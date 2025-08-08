package com.fpt.capstone.tourism.dto.response.accountant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingServiceSettlementDTO {
    private String serviceName;
    private Integer dayNumber;
    private Integer pax;
    private Double costPerPax;
    private Double sellingPrice;
}
