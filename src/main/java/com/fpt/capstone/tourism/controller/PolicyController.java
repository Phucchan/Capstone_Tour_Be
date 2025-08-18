package com.fpt.capstone.tourism.controller;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.PolicyDTO;
import com.fpt.capstone.tourism.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    //postman http://localhost:8080/public/policies
    public ResponseEntity<GeneralResponse<List<PolicyDTO>>> getPolicies() {
        return ResponseEntity.ok(policyService.getPolicies());
    }
}