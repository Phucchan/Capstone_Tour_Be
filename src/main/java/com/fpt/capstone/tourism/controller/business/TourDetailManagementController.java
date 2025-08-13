package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class TourDetailManagementController {

    private final TourManagementService tourManagementService;

    // xem chi tiết tour
    // postman http://localhost:8080/v1/business/tours/1
    @GetMapping("/tours/{id}")
    public ResponseEntity<GeneralResponse<TourDetailOptionsDTO>> getTourDetail(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getTourDetail(id));
    }

    // cập nhật chi tiết tour
    // postman http://localhost:8080/v1/business/tours/1
    @PutMapping(value = "/tours/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<GeneralResponse<TourDetailManagerDTO>> updateTour(
            @PathVariable Long id,
            @RequestPart("tourData") TourUpdateManagerRequestDTO requestDTO,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile file) {
        return ResponseEntity.ok(tourManagementService.updateTour(id, requestDTO, file));
    }

}
