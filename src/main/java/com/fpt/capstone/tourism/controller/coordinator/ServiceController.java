package com.fpt.capstone.tourism.controller.coordinator;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ServiceCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.ServiceDetailDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.model.enums.PartnerServiceStatus;
import com.fpt.capstone.tourism.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coordinator/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @GetMapping
    public ResponseEntity<GeneralResponse<PagingDTO<ServiceInfoDTO>>> getServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(serviceManagementService.getServices(page, size, keyword));
    }
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<ServiceDetailDTO>> getServiceDetail(@PathVariable Long id) {
        return ResponseEntity.ok(serviceManagementService.getServiceDetail(id));
    }
    @PostMapping
    public ResponseEntity<GeneralResponse<ServiceInfoDTO>> createService(@RequestBody ServiceCreateRequestDTO dto) {
        return ResponseEntity.ok(serviceManagementService.createService(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<String>> deleteService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceManagementService.deleteService(id));
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<GeneralResponse<ServiceInfoDTO>> changeServiceStatus(
            @PathVariable Long id,
            @RequestParam PartnerServiceStatus status) {
        return ResponseEntity.ok(serviceManagementService.changeServiceStatus(id, status));
    }
}