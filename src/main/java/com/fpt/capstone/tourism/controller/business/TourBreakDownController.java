package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.request.tourManager.TourDayManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business/")
public class TourBreakDownController {

    @Autowired
    private final TourManagementService tourManagementService;

    // lấy ra danh sách dịch vụ của tour
    @GetMapping("/tours/{id}/services")
    public ResponseEntity<GeneralResponse<List<ServiceBreakdownDTO>>> getTourServices(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getServiceBreakdown(id));
    }

    // Tạo cấu hình số lượng khách cho tour
    // postman http://localhost:8080/v1/business/tours/1/pax
    @PostMapping("/tours/{id}/pax")
    public ResponseEntity<GeneralResponse<TourPaxManagerDTO>> createTourPax(@PathVariable Long id,
                                                                            @RequestBody TourPaxManagerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.createTourPax(id, requestDTO));
    }

}
