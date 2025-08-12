package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.PartnerServiceCreateDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    // Lấy danh sách các ngày trong tour
    @GetMapping("/tours/{id}/days")
    // postman http://localhost:8080/v1/business/tours/1/days
    public ResponseEntity<GeneralResponse<List<TourDayManagerDTO>>> getTourDays(@PathVariable Long id) {
        return ResponseEntity.ok(tourManagementService.getTourDays(id));
    }

    // Tạo một ngày trong tour
    //postman http://localhost:8080/v1/business/tours/1/days
    @PostMapping("/tours/{id}/days")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> createTourDay(@PathVariable Long id,
                                                                            @RequestBody TourDayManagerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.createTourDay(id, requestDTO));
    }

    // Cập nhật một ngày trong tour
    // postman http://localhost:8080/v1/business/tours/1/days/1
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
    @GetMapping("/service-types")
    // postman http://localhost:8080/v1/business/service-types
    public ResponseEntity<GeneralResponse<List<ServiceTypeShortDTO>>> getServiceTypes() {
        return ResponseEntity.ok(tourManagementService.getServiceTypes());
    }
    // Thêm dịch vụ vào một ngày của tour
    // postman http://localhost:8080/v1/business/tours/1/days/1/services/1
    @PostMapping("/tours/{tourId}/days/{dayId}/services/{serviceId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> addServiceToTourDay(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(tourManagementService.addServiceToTourDay(tourId, dayId, serviceId));
    }

    // Xóa dịch vụ khỏi một ngày của tour
    // postman http://localhost:8080/v1/business/tours/1/days/1/services/1
    @DeleteMapping("/tours/{tourId}/days/{dayId}/services/{serviceId}")
    public ResponseEntity<GeneralResponse<TourDayManagerDTO>> removeServiceFromTourDay(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(tourManagementService.removeServiceFromTourDay(tourId, dayId, serviceId));
    }
    @PostMapping("/tours/{tourId}/days/{dayId}/services")
    // postman http://localhost:8080/v1/business/tours/1/days/1/services
    public ResponseEntity<GeneralResponse<ServiceInfoDTO>> createService(
            @PathVariable Long tourId,
            @PathVariable Long dayId,
            @RequestBody PartnerServiceCreateDTO dto) {
        return ResponseEntity.ok(tourManagementService.createService(tourId, dayId, dto));
    }

}
