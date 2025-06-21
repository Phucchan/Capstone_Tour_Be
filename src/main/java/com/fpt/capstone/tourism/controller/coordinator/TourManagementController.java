package com.fpt.capstone.tourism.controller.coordinator;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.response.TourResponseDTO;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coordinator/")
public class TourManagementController {

    @Autowired
    private final TourManagementService tourManagementService;

    @GetMapping("/tours")
    public ResponseEntity<GeneralResponse<List<TourResponseDTO>>> getListtours() {
        return ResponseEntity.ok(tourManagementService.getListTours());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<GeneralResponse<Object>> changeStatus(@PathVariable Long id, @RequestBody ChangeStatusDTO changeStatusDTO) {
        return ResponseEntity.ok(tourManagementService.changeStatus(id, changeStatusDTO));
    }

}
