package com.fpt.capstone.tourism.controller.booking;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.service.RequestBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/request-bookings")
public class RequestBookingController {

    private final RequestBookingService requestBookingService;

    @PostMapping
    public ResponseEntity<GeneralResponse<RequestBooking>> create(@RequestBody RequestBookingDTO requestBookingDTO) {
        return ResponseEntity.ok(requestBookingService.createRequest(requestBookingDTO));
    }
}