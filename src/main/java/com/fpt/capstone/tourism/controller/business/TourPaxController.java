package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.common.PartnerServiceShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPriceCalculateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxFullDTO;
import com.fpt.capstone.tourism.service.PartnerServiceService;
import com.fpt.capstone.tourism.service.TourPaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.fpt.capstone.tourism.dto.response.tourManager.ServiceBreakdownDTO;
import com.fpt.capstone.tourism.service.TourManagementService;
import org.springframework.http.HttpStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class TourPaxController {
    private final TourPaxService tourPaxService;
    private final PartnerServiceService partnerServiceService;
    private final TourManagementService tourManagementService;


    @GetMapping("/tour/{tourId}/tour-pax")
    // Example: http://localhost:8080/v1/business/tour/1/tour-pax
    public ResponseEntity<GeneralResponse<List<TourPaxFullDTO>>> getTourPaxConfigurations(
            @PathVariable Long tourId) {
        return ResponseEntity.ok(tourPaxService.getTourPaxConfigurations(tourId));
    }

    @GetMapping("/tour/{tourId}/tour-pax/detail/{paxId}")
    // Example: http://localhost:8080/v1/business/tour/1/tour-pax/detail/1
    public ResponseEntity<GeneralResponse<TourPaxFullDTO>> getTourPaxConfiguration(
            @PathVariable Long tourId,
            @PathVariable Long paxId) {
        return ResponseEntity.ok(tourPaxService.getTourPaxConfiguration(tourId, paxId));
    }

    @PostMapping("/tour/{tourId}/tour-pax/create")
    // Example: http://localhost:8080/v1/business/tour/1/tour-pax/create
    public ResponseEntity<GeneralResponse<TourPaxFullDTO>> createTourPaxConfiguration(
            @PathVariable Long tourId,
            @RequestBody TourPaxCreateRequestDTO request) {
        return ResponseEntity.ok(tourPaxService.createTourPaxConfiguration(tourId, request));
    }

    @PutMapping("/tour/{tourId}/tour-pax/update/{paxId}")
    public ResponseEntity<GeneralResponse<TourPaxFullDTO>> updateTourPaxConfiguration(
            @PathVariable Long tourId,
            @PathVariable Long paxId,
            @RequestBody TourPaxUpdateRequestDTO request) {
        return ResponseEntity.ok(tourPaxService.updateTourPaxConfiguration(tourId, paxId, request));
    }

    @DeleteMapping("/tour/{tourId}/tour-pax/{paxId}")
    public ResponseEntity<GeneralResponse<String>> deleteTourPaxConfiguration(
            @PathVariable Long tourId,
            @PathVariable Long paxId) {
        return ResponseEntity.ok(tourPaxService.deleteTourPaxConfiguration(tourId, paxId));
    }
    @PostMapping("/tour/{tourId}/tour-pax/calculate-prices")
    public ResponseEntity<GeneralResponse<List<TourPaxFullDTO>>> calculatePrices(
            @PathVariable Long tourId,
            @RequestBody TourPriceCalculateRequestDTO request) {
        return ResponseEntity.ok(tourPaxService.calculatePrices(tourId, request));
    }
    @GetMapping("/partner-services")
    // Example: http://localhost:8080/v1/business/partner-services
    public ResponseEntity<GeneralResponse<List<PartnerServiceShortDTO>>> getPartnerServices() {
        return ResponseEntity.ok(partnerServiceService.getPartnerServices());
    }

    /**
     * API để lấy danh sách chi tiết các dịch vụ đã được thêm vào tour,
     * dùng cho trang chiết tính.
     */
    @GetMapping("/tours/{tourId}/services")
    public ResponseEntity<GeneralResponse<List<ServiceBreakdownDTO>>> getServiceBreakdown(
            @PathVariable Long tourId) {
        return ResponseEntity.ok(tourManagementService.getServiceBreakdown(tourId));
    }

//    @GetMapping("/markup")
//    public ResponseEntity<GeneralResponse<TourMarkupResponseDTO>> getTourMarkupPercentage(
//            @PathVariable Long tourId) {
//        return ResponseEntity.ok(tourService.getTourMarkupPercentage(tourId));
//    }
//
//    @PutMapping("/update-markup")
//    public ResponseEntity<GeneralResponse<TourResponseDTO>> updateTourMarkupPercentage(
//            @PathVariable Long tourId,
//            @RequestBody TourMarkupUpdateRequestDTO request) {
//        return ResponseEntity.ok(tourService.updateTourMarkupPercentage(tourId, request.getMarkUpPercent()));
//    }
}