package com.fpt.capstone.tourism.dto.request.plan;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanGenerationRequestDTO {
    private int userId;
    private String startDate;
    private String endDate;
    private List<String> preferences;
    private String planType;
    private boolean travelingWithChildren;
    private boolean isTravelingWithChildren;
    private List<PlanDayDTO> days;
    private BudgetDTO budget;
}
