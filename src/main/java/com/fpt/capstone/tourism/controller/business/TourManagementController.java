package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.TourUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.TourDayDTO;
import com.fpt.capstone.tourism.dto.response.TourDetailDTO;
import com.fpt.capstone.tourism.dto.response.TourResponseDTO;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business/")
public class TourManagementController {

    @Autowired
    private final TourManagementService tourManagementService;

    @GetMapping("/tours")
    // "id": 6,
    //            "name": "Nha Trang biển gọi",
    //            "typeName": "FIXED",
    //            "tourStatus": "PUBLISHED"
    public ResponseEntity<GeneralResponse<List<TourResponseDTO>>> getListtours() {
        return ResponseEntity.ok(tourManagementService.getListTours());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<GeneralResponse<Object>> changeStatus(@PathVariable Long id, @RequestBody ChangeStatusDTO changeStatusDTO) {
        return ResponseEntity.ok(tourManagementService.changeStatus(id, changeStatusDTO));
    }
    @GetMapping("/tours/{id}")
    public ResponseEntity<GeneralResponse<Object>> getTourDetail(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getTourDetail(id));
    }

    @PutMapping("/tours/{id}")
    public ResponseEntity<GeneralResponse<TourDetailDTO>> updateTour(@PathVariable Long id,
                                                                     @RequestBody TourUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.updateTour(id, requestDTO));
    }
    @GetMapping("/tours/{id}/days")
    public ResponseEntity<GeneralResponse<List<TourDayDTO>>> getTourDays(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getTourDays(id));
    }

}
