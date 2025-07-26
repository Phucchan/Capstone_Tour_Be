package com.fpt.capstone.tourism.controller.coordinator;

import com.fpt.capstone.tourism.dto.common.ServiceTypeDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeDeleteStatusDTO;
import com.fpt.capstone.tourism.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cordinator")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @GetMapping("/service-types")
    // Example: http://localhost:8080/v1/cordinator/service-types
    public ResponseEntity<GeneralResponse<List<ServiceTypeDTO>>> getServiceTypes() {
        return ResponseEntity.ok(serviceTypeService.getServiceTypes());
    }
    @PostMapping("/service-types")
    public ResponseEntity<GeneralResponse<ServiceTypeDTO>> createServiceType(@RequestBody ServiceTypeDTO dto) {
        return ResponseEntity.ok(serviceTypeService.createServiceType(dto));
    }

    @PutMapping("/service-types/{id}")
    public ResponseEntity<GeneralResponse<ServiceTypeDTO>> updateServiceType(@PathVariable Long id,
                                                                             @RequestBody ServiceTypeDTO dto) {
        return ResponseEntity.ok(serviceTypeService.updateServiceType(id, dto));
    }

    @DeleteMapping("/service-types/{id}")
    public ResponseEntity<GeneralResponse<String>> deleteServiceType(@PathVariable Long id) {
        return ResponseEntity.ok(serviceTypeService.deleteServiceType(id));
    }

    @PatchMapping("/service-types/{id}/status")
    public ResponseEntity<GeneralResponse<ServiceTypeDTO>> changeStatus(@PathVariable Long id,
                                                                        @RequestBody ChangeDeleteStatusDTO changeStatusDTO) {
        return ResponseEntity.ok(serviceTypeService.changeStatus(id, changeStatusDTO));
    }
}