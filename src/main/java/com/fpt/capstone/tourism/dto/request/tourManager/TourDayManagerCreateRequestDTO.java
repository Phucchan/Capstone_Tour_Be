package com.fpt.capstone.tourism.dto.request.tourManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourDayManagerCreateRequestDTO {
    private String title;
    private Long locationId;
    private List<Long> serviceTypeIds;
    private String description;
}