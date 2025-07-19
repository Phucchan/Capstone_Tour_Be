package com.fpt.capstone.tourism.service.plan;

import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;

import java.util.List;

public interface PlanService {
    List<PublicLocationDTO> getLocations();
}
