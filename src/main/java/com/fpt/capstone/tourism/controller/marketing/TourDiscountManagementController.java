package com.fpt.capstone.tourism.controller.marketing;

import com.fpt.capstone.tourism.dto.common.TourDiscountDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.TourDiscountRequestDTO;
import com.fpt.capstone.tourism.dto.response.TourDiscountSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourResponseManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleManagerDTO;
import com.fpt.capstone.tourism.service.TourDiscountService;
import com.fpt.capstone.tourism.service.TourScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marketing/discounts")
public class TourDiscountManagementController {

    private final TourDiscountService tourDiscountService;
    private final TourScheduleService tourScheduleService;

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteDiscount(@PathVariable Long id) {
        return ResponseEntity.ok(tourDiscountService.deleteDiscount(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<TourDiscountDTO>> updateDiscount(@PathVariable Long id, @Valid @RequestBody TourDiscountRequestDTO dto) {
        return ResponseEntity.ok(tourDiscountService.updateDiscount(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<TourDiscountDTO>> getDiscountById(@PathVariable Long id) {
        return ResponseEntity.ok(tourDiscountService.getDiscountById(id));
    }

    @PostMapping
    //postman http://localhost:8080/v1/marketing/discounts
    public ResponseEntity<GeneralResponse<TourDiscountDTO>> createDiscount(@Valid @RequestBody TourDiscountRequestDTO dto) {
        return ResponseEntity.ok(tourDiscountService.createDiscount(dto));
    }


    @GetMapping("/tours")
    //postman http://localhost:8080/v1/marketing/discounts/tours?page=0&size=10
    public ResponseEntity<GeneralResponse<PagingDTO<TourResponseManagerDTO>>> getTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean hasDiscount) {
        return ResponseEntity.ok(tourDiscountService.getToursForDiscount(keyword, page, size, hasDiscount));
    }

    @GetMapping("/tours/{tourId}/schedules")
    //postman http://localhost:8080/v1/marketing/discounts/tours/1/schedules
    public ResponseEntity<GeneralResponse<List<TourScheduleManagerDTO>>> getSchedules(@PathVariable Long tourId) {
        return ResponseEntity.ok(tourScheduleService.getTourSchedules(tourId));
    }
}