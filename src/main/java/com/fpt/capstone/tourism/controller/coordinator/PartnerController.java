package com.fpt.capstone.tourism.controller.coordinator;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.PartnerDetailDTO;
import com.fpt.capstone.tourism.dto.response.PartnerSummaryDTO;
import com.fpt.capstone.tourism.service.PartnerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cordinator")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerManagementService partnerManagementService;

    @GetMapping("/partners")
    public ResponseEntity<GeneralResponse<PagingDTO<PartnerSummaryDTO>>> getPartners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(defaultValue = "deleted") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(partnerManagementService.getPartners(page, size, keyword, isDeleted, sortField, sortDirection));
    }
    @GetMapping("/partners/{id}")
    // postman http://localhost:8080/cordinator/partners/1
    public ResponseEntity<GeneralResponse<PartnerDetailDTO>> getPartnerDetail(@PathVariable Long id) {
        return ResponseEntity.ok(partnerManagementService.getPartnerDetail(id));
    }
}