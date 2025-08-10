package com.fpt.capstone.tourism.controller.accountant;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.accountatn.CreateBillRequestDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingListDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingSettlementDTO;
import com.fpt.capstone.tourism.service.AccountantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{bookingId}/settlement")
    // Example request: http://localhost:8080/v1/accountant/bookings/123/settlement
    public ResponseEntity<GeneralResponse<BookingSettlementDTO>> getBookingSettlement(@PathVariable Long bookingId) {
        return ResponseEntity.ok(accountantService.getBookingSettlement(bookingId));
    }
    @PostMapping("/{bookingId}/receipt-bill")
    public ResponseEntity<GeneralResponse<BookingSettlementDTO>> createReceiptBill(
            @PathVariable Long bookingId,
            @RequestBody CreateBillRequestDTO request) {
        return ResponseEntity.ok(accountantService.createReceiptBill(bookingId, request));
    }

    @PostMapping("/{bookingId}/payment-bill")
    public ResponseEntity<GeneralResponse<BookingSettlementDTO>> createPaymentBill(
            @PathVariable Long bookingId,
            @RequestBody CreateBillRequestDTO request) {
        return ResponseEntity.ok(accountantService.createPaymentBill(bookingId, request));
    }
    @PatchMapping("/bills/{billId}/paid")
    public ResponseEntity<GeneralResponse<String>> markBillPaid(@PathVariable Long billId) {
        return ResponseEntity.ok(accountantService.markBillPaid(billId));
    }
}
