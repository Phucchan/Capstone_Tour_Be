package com.fpt.capstone.tourism.controller.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.service.RequestBookingService;
import com.fpt.capstone.tourism.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/{userId}")
public class PrivateTourController {

    private final RequestBookingService requestBookingService;
    private final TourService tourService;

    @GetMapping("/request-bookings")
    //postman http://localhost:8080/v1/customer/1/request-bookings?page=0&size=10
    public ResponseEntity<GeneralResponse<PagingDTO<RequestBookingSummaryDTO>>> getRequestBookings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(
                requestBookingService.getRequestsByUser(userId, page, size, search)
        );
    }

    @GetMapping("/custom-tours")
    //postman http://localhost:8080/v1/customer/1/custom-tours?page=0&size=10
    public ResponseEntity<GeneralResponse<PagingDTO<TourSummaryDTO>>> getCustomTours(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        PagingDTO<TourSummaryDTO> paging = tourService.getCustomToursByUser(userId, search, pageable);
        return ResponseEntity.ok(GeneralResponse.of(paging));
    }
}