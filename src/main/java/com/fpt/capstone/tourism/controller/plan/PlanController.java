package com.fpt.capstone.tourism.controller.plan;


import com.fpt.capstone.tourism.dto.common.PlanDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.plan.PlanGenerationRequestDTO;
import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
import com.fpt.capstone.tourism.model.mongo.Plan;
import com.fpt.capstone.tourism.service.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/generate")
    public ResponseEntity<GeneralResponse<Plan>> generatePlan(@RequestBody PlanGenerationRequestDTO dto) {
        return ResponseEntity.ok(GeneralResponse.of(planService.generatePlan(dto)));
    }

    @PostMapping("/save")
    public ResponseEntity<GeneralResponse<String>> savePlan(@RequestBody String id) {

        return ResponseEntity.ok(GeneralResponse.of(planService.savePlan(id)));
    }

    @GetMapping("/details/{planId}")
    public ResponseEntity<GeneralResponse<Plan>> getPlanDetails(@PathVariable String planId) {
        return ResponseEntity.ok(GeneralResponse.of(planService.getPlanById(planId)));
    }

    @GetMapping("/list")
    public ResponseEntity<GeneralResponse<PagingDTO<Plan>>> getPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") Integer userId,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(planService.getPlans(page, size, sortField, sortDirection, userId));
    }


}
