package com.fpt.capstone.tourism.service.plan;

import com.fpt.capstone.tourism.dto.request.plan.PlanGenerationRequestDTO;
import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
import com.fpt.capstone.tourism.model.mongo.Plan;

import java.util.List;

public interface PlanService {
    List<PublicLocationDTO> getLocations();

    Plan generatePlan(PlanGenerationRequestDTO dto);

    Plan getPlanById(String planId);
}
