package com.fpt.capstone.tourism.controller.marketing;

import com.fpt.capstone.tourism.dto.common.TourDiscountDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.TourDiscountRequestDTO;
import com.fpt.capstone.tourism.dto.response.TourDiscountSummaryDTO;
import com.fpt.capstone.tourism.service.TourDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marketing/discounts")
public class TourDiscountManagementController {

    private final TourDiscountService tourDiscountService;

    @PostMapping
    //postman http://localhost:8080/v1/marketing/discounts
    public ResponseEntity<GeneralResponse<TourDiscountDTO>> createDiscount(@Valid @RequestBody TourDiscountRequestDTO dto) {
        return ResponseEntity.ok(tourDiscountService.createDiscount(dto));
    }

    @GetMapping
    //postman http://localhost:8080/v1/marketing/discounts?page=0&size=10&keyword=tour
    public ResponseEntity<GeneralResponse<PagingDTO<TourDiscountSummaryDTO>>> getDiscounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(tourDiscountService.getDiscounts(keyword, page, size));
    }
}