package com.fpt.capstone.tourism.dto.response.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourThemeOptionDTO {
    private Long id;
    private String name;
}