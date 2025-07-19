package com.fpt.capstone.tourism.dto.request.plan;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetDTO {
    private double min;
    private double max;
}
