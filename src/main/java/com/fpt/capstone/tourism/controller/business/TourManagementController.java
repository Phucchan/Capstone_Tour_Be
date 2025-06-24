package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
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
public class TourManagementController {

    @Autowired
    private final TourManagementService tourManagementService;

    // danh sách tour
    // postman http://localhost:8080/v1/business/tours
    @GetMapping("/tours")
    public ResponseEntity<GeneralResponse<PagingDTO<TourResponseManagerDTO>>> getListtours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(tourManagementService.getListTours(page, size));
    }


    // thay đổi trạng thái tour
    // postman http://localhost:8080/v1/business/tours/1/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<GeneralResponse<Object>> changeStatus(@PathVariable Long id, @RequestBody ChangeStatusDTO changeStatusDTO) {
        return ResponseEntity.ok(tourManagementService.changeStatus(id, changeStatusDTO));
    }

    // xem chi tiết tour
    // postman http://localhost:8080/v1/business/tours/1
    @GetMapping("/tours/{id}")
    public ResponseEntity<GeneralResponse<Object>> getTourDetail(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getTourDetail(id));
    }

    // cập nhật chi tiết tour
    // postman http://localhost:8080/v1/business/tours/1
    @PutMapping("/tours/{id}")
    public ResponseEntity<GeneralResponse<TourDetailManagerDTO>> updateTour(@PathVariable Long id,
                                                                            @RequestBody TourUpdateManagerRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.updateTour(id, requestDTO));
    }

    // Lấy danh sách các ngày trong tour
    @GetMapping("/tours/{id}/days")
    // postman http://localhost:8080/v1/business/tours/1/days
    public ResponseEntity<GeneralResponse<List<TourDayManagerDTO>>> getTourDays(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getTourDays(id));
    }

    // Tạo một ngày trong tour
    //postman http://localhost:8080/business/tours/1/days
    @PostMapping("/tours/{id}/days")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> createTourDay(@PathVariable Long id,
                                                                            @RequestBody TourDayManagerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.createTourDay(id, requestDTO));
    }

    // Cập nhật một ngày trong tour
    // postman http://localhost:8080/v1/business/tours/1/days/1
    //body { "title": "Ngày 1", "locationId": 1, "serviceIds": [1, 2], "description": "Mô tả ngày 1" }
    @PutMapping("/tours/{tourId}/days/{dayId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> updateTourDay(@PathVariable Long tourId,
                                                                            @PathVariable Long dayId,
                                                                            @RequestBody TourDayManagerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.updateTourDay(tourId, dayId, requestDTO));
    }

    // Xóa một ngày trong tour
    //postman http://localhost:8080/v1/business/tours/1/days/1
    @DeleteMapping("/tours/{tourId}/days/{dayId}")
    public ResponseEntity<GeneralResponse<String>> deleteTourDay(@PathVariable Long tourId,
                                                                 @PathVariable Long dayId) {
        return ResponseEntity.ok(tourManagementService.deleteTourDay(tourId, dayId));
    }

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
