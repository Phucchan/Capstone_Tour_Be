package com.fpt.capstone.tourism.controller.plan;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
import com.fpt.capstone.tourism.service.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/plans")
public class PlanController {

    private final PlanService planService;


    @GetMapping("/locations")
    public ResponseEntity<GeneralResponse<List<PublicLocationDTO>>> locations() {
        return ResponseEntity.ok(GeneralResponse.of(planService.getLocations()));
    }


}
