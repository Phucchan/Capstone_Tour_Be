package com.fpt.capstone.tourism.controller.coordinator;

import com.fpt.capstone.tourism.dto.common.ServiceTypeDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cordinator")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @GetMapping("/service-types")
    public ResponseEntity<GeneralResponse<List<ServiceTypeDTO>>> getServiceTypes() {
        return ResponseEntity.ok(serviceTypeService.getServiceTypes());
    }
}