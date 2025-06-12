package com.fpt.capstone.tourism.dto.common;

import com.fpt.capstone.tourism.model.enums.PlanStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanDTO {
    private Long id;
    private String content;
    private PlanStatus planStatus;
}
