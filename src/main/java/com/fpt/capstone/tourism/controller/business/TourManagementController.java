package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;

import com.fpt.capstone.tourism.dto.request.tourManager.TourCreateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourOptionsDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class TourManagementController {

    @Autowired
    private final TourManagementService tourManagementService;

    // danh sách tour
    // postman http://localhost:8080/v1/business/tours?page=1&size=6
    @GetMapping("/tours")
    public ResponseEntity<GeneralResponse<PagingDTO<TourResponseManagerDTO>>> getListtours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TourType tourType,
            @RequestParam(required = false) TourStatus tourStatus) {
        return ResponseEntity.ok(tourManagementService.getListTours(page, size, keyword, tourType, tourStatus));

    }

    // thay đổi trạng thái tour
    // postman http://localhost:8080/v1/business/tours/1/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<GeneralResponse<Object>> changeStatus(@PathVariable Long id, @RequestBody ChangeStatusDTO changeStatusDTO) {
        return ResponseEntity.ok(tourManagementService.changeStatus(id, changeStatusDTO));
    }

    @PostMapping("/tours")
    // postman http://localhost:8080/v1/business/tours
    public ResponseEntity<GeneralResponse<TourDetailManagerDTO>> createTour(@RequestBody TourCreateManagerRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.createTour(requestDTO));
    }

    @GetMapping("/tours/options")
    // postman http://localhost:8080/v1/business/tours/options
    public ResponseEntity<GeneralResponse<TourOptionsDTO>> getTourOptions() {
        return ResponseEntity.ok(tourManagementService.getTourOptions());
    }


}
