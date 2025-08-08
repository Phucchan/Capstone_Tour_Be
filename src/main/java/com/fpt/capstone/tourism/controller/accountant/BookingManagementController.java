package com.fpt.capstone.tourism.controller.accountant;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingListDTO;
import com.fpt.capstone.tourism.service.AccountantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accountant/bookings")
public class BookingManagementController {

    private final AccountantService accountantService;

    @GetMapping
    // Example request: http://localhost:8080/v1/accountant/bookings?search=keyword&page=0&size=10
    public ResponseEntity<GeneralResponse<PagingDTO<BookingListDTO>>> getBookings(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(accountantService.getBookings(search, page, size));
    }
}
