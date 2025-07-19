package com.fpt.capstone.tourism.dto.request.plan;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PlanDayDTO {
    private int dayNumber;
    private int locationId;
    private String locationName;
}
