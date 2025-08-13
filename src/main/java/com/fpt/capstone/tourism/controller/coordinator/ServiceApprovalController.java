package com.fpt.capstone.tourism.controller.coordinator;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.PendingServiceUpdateDTO;
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
    public ResponseEntity<GeneralResponse<PagingDTO<ServiceInfoDTO>>> getPendingServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(approvalService.getPendingServices(page, size, keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<ServiceInfoDTO>> updateService(@PathVariable Long id,
                                                                         @RequestBody PendingServiceUpdateDTO dto) {
        return ResponseEntity.ok(approvalService.updateService(id, dto));
    }
}