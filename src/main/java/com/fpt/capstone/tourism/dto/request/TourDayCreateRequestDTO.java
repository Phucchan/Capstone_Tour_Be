package com.fpt.capstone.tourism.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourDayCreateRequestDTO {
    private String title;
    private Long locationId;
    private List<Long> serviceIds;
    private String description;
}