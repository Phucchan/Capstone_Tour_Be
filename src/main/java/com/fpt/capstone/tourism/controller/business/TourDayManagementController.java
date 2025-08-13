package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.PartnerServiceCreateDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourDayManagerDTO;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class TourDayManagementController {

    private final TourManagementService tourManagementService;

    // Lấy danh sách các ngày trong tour
    @GetMapping("/tours/{id}/days")
    public ResponseEntity<GeneralResponse<List<TourDayManagerDTO>>> getTourDays(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getTourDays(id));
    }

    // Tạo một ngày trong tour
    @PostMapping("/tours/{id}/days")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> createTourDay(@PathVariable Long id,
                                                                            @RequestBody TourDayManagerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.createTourDay(id, requestDTO));
    }

    // Cập nhật một ngày trong tour
    @PutMapping("/tours/{tourId}/days/{dayId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> updateTourDay(@PathVariable Long tourId,
                                                                            @PathVariable Long dayId,
                                                                            @RequestBody TourDayManagerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.updateTourDay(tourId, dayId, requestDTO));
    }

    // Xóa một ngày trong tour
    @DeleteMapping("/tours/{tourId}/days/{dayId}")
    //postman http://localhost:8080/business/tours/1/days/1
    public ResponseEntity<GeneralResponse<String>> deleteTourDay(@PathVariable Long tourId,
                                                                 @PathVariable Long dayId) {
        return ResponseEntity.ok(tourManagementService.deleteTourDay(tourId, dayId));
    }

    // Lấy danh sách loại dịch vụ
    @GetMapping("/service-types")
    public ResponseEntity<GeneralResponse<List<ServiceTypeShortDTO>>> getServiceTypes() {
        return ResponseEntity.ok(tourManagementService.getServiceTypes());
    }

    // Thêm dịch vụ vào một ngày của tour
    @PostMapping("/tours/{tourId}/days/{dayId}/services/{serviceId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> addServiceToTourDay(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(tourManagementService.addServiceToTourDay(tourId, dayId, serviceId));
    }

    // Xóa dịch vụ khỏi một ngày của tour
    @DeleteMapping("/tours/{tourId}/days/{dayId}/services/{serviceId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> removeServiceFromTourDay(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(tourManagementService.removeServiceFromTourDay(tourId, dayId, serviceId));
    }

    // Tạo mới dịch vụ cho một ngày của tour
    @PostMapping("/tours/{tourId}/days/{dayId}/services")
    //postman http://localhost:8080/business/tours/2/days/1/services
    public ResponseEntity<GeneralResponse<ServiceInfoDTO>> createService(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @RequestBody PartnerServiceCreateDTO dto) {
        return ResponseEntity.ok(tourManagementService.createService(tourId, dayId, dto));
    }

}

