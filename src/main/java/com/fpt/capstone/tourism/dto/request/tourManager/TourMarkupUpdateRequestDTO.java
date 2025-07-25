package com.fpt.capstone.tourism.dto.request.tourManager;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourMarkupUpdateRequestDTO {
    private Double markUpPercent;
}
