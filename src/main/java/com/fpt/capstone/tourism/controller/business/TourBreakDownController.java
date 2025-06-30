package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayServiceUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.ServiceBreakdownDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourDayManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateManagerRequestDTO;
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

    // Lấy ra danh sách dịch vụ của tour
    // postman http://localhost:8080/v1/business/tours/1/services
    @GetMapping("/tours/{id}/services")
    public ResponseEntity<GeneralResponse<List<ServiceBreakdownDTO>>> getTourServices(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getServiceBreakdown(id));
    }
    // Thêm dịch vụ vào một ngày trong tour
    //  postman http://localhost:8080/v1/business/tours/1/days/1/services/1
    @PostMapping("/tours/{tourId}/days/{dayId}/services/{serviceId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> addServiceToTourDay(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(tourManagementService.addServiceToTourDay(tourId, dayId, serviceId));
    }

    // Cập nhật dịch vụ của ngày tour
    @PutMapping("/tours/{tourId}/days/{dayId}/services/{serviceId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> updateServiceInTourDay(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @PathVariable Long serviceId,
            @RequestBody TourDayServiceUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.updateServiceInTourDay(tourId, dayId, serviceId, requestDTO.getNewServiceId()));
    }

    // Xóa dịch vụ khỏi ngày tour
    @DeleteMapping("/tours/{tourId}/days/{dayId}/services/{serviceId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> removeServiceFromTourDay(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(tourManagementService.removeServiceFromTourDay(tourId, dayId, serviceId));
    }


    //postman http://localhost:8080/v1/business/tours/1/pax/1
    @GetMapping("/tours/{tourId}/pax/{paxId}")
    public ResponseEntity<GeneralResponse<TourPaxManagerDTO>> getTourPax(@PathVariable Long tourId,
                                                                         @PathVariable Long paxId) {
        return ResponseEntity.ok(tourManagementService.getTourPax(tourId, paxId));
    }
    // Tạo cấu hình số lượng khách cho tour
    // postman http://localhost:8080/v1/business/tours/1/pax
    @PostMapping("/tours/{tourId}/pax")
    public ResponseEntity<GeneralResponse<TourPaxManagerDTO>> createTourPax(
            @PathVariable("tourId") Long tourId,
            @RequestBody TourPaxManagerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.createTourPax(tourId, requestDTO));
    }

    // Cập nhật cấu hình số lượng khách
    // postman http://localhost:8080/v1/business/tours/1/pax/1
    @PutMapping("/tours/{tourId}/pax/{paxId}")
    public ResponseEntity<GeneralResponse<TourPaxManagerDTO>> updateTourPax(
            @PathVariable Long tourId,
            @PathVariable Long paxId,
            @RequestBody TourPaxManagerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.updateTourPax(tourId, paxId, requestDTO));
    }

    // Xóa cấu hình số lượng khách
    // postman http://localhost:8080/v1/business/tours/1/pax/1
    @DeleteMapping("/tours/{tourId}/pax/{paxId}")
    public ResponseEntity<GeneralResponse<String>> deleteTourPax(
            @PathVariable Long tourId,
            @PathVariable Long paxId) {
        return ResponseEntity.ok(tourManagementService.deleteTourPax(tourId, paxId));
    }
}
