package com.fpt.capstone.tourism.controller.accountant;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDetailDTO;
import com.fpt.capstone.tourism.service.AccountantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accountant/refunds")
public class AccountantRefundController {

    private final AccountantService accountantService;

    @GetMapping
    //postman http://localhost:8080/v1/accountant/refunds?search=keyword&page=0&size=10
    public ResponseEntity<GeneralResponse<PagingDTO<BookingRefundDTO>>> getRefundRequests(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(accountantService.getRefundRequests(search, page, size));
    }
    @GetMapping("/{bookingId}")
    //postman http://localhost:8080/v1/accountant/refunds/1
    public ResponseEntity<GeneralResponse<BookingRefundDetailDTO>> getRefundRequestDetail(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(accountantService.getRefundRequestDetail(bookingId));
    }
    @PatchMapping("/{bookingId}/cancel")
    //postman http://localhost:8080/v1/accountant/refunds/1/cancel
    public ResponseEntity<GeneralResponse<BookingRefundDetailDTO>> cancelRefundRequest(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(accountantService.cancelRefundRequest(bookingId));
    }
}

