package com.fpt.capstone.tourism.controller.coordinator;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.service.PartnerServiceApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coordinator/services")
public class ServiceApprovalController {

    private final PartnerServiceApprovalService approvalService;

    @GetMapping("/pending")
    public ResponseEntity<GeneralResponse<List<ServiceInfoDTO>>> getPendingServices() {
        return ResponseEntity.ok(approvalService.getPendingServices());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<GeneralResponse<ServiceInfoDTO>> changeStatus(@PathVariable Long id,
                                                                        @RequestBody ChangeStatusDTO dto) {
        return ResponseEntity.ok(approvalService.changeStatus(id, dto));
    }
}