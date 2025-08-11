package com.fpt.capstone.tourism.controller.booking;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.service.RequestBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("customer/request-bookings")
public class RequestBookingController {

    private final RequestBookingService requestBookingService;

    @PostMapping
    // Example request: POST http://localhost:8080/v1/request-bookings?userId=123
    public ResponseEntity<GeneralResponse<RequestBookingDTO>> createRequest(
            @RequestParam("userId") Long userId,
            @RequestBody RequestBookingDTO requestBookingDTO) {
        requestBookingDTO.setUserId(userId);
        return ResponseEntity.ok(requestBookingService.createRequest(requestBookingDTO));
    }
    @GetMapping("/notification/{id}")
    public ResponseEntity<GeneralResponse<RequestBookingDTO>> getNotification(@PathVariable("id") Long id) {
        return ResponseEntity.ok(requestBookingService.getRequest(id));
    }
}