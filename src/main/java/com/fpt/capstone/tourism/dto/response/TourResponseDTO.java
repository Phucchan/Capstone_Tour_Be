package com.fpt.capstone.tourism.dto.response;

import com.fpt.capstone.tourism.model.enums.TourStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseDTO {
    private Long id;
    private String name;
    private String thumbnailImage;
    private double price;
    private String typeName;
    private String tourStatus;
}
