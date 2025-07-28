package com.fpt.capstone.tourism.controller.seller;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.seller.BookingCustomerUpdateDTO;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingSummaryDTO;
import com.fpt.capstone.tourism.service.SellerBookingService;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/bookings")
public class SellerBookingController {

    private final SellerBookingService sellerBookingService;

    @GetMapping("/available")
    public ResponseEntity<GeneralResponse<PagingDTO<SellerBookingSummaryDTO>>> getAvailableBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sellerBookingService.getAvailableBookings(page, size));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<GeneralResponse<Booking>> updateBooking(
            @PathVariable Long bookingId,
            @RequestParam String sellerUsername,
            @RequestBody(required = false) List<BookingCustomerUpdateDTO> customers) {
        return ResponseEntity.ok(sellerBookingService.updateBooking(bookingId, sellerUsername, customers));
    }

    @GetMapping("/edited")
    //sellerUsername=wangbinh
    public ResponseEntity<GeneralResponse<PagingDTO<SellerBookingSummaryDTO>>> getEditedTours(
            @RequestParam String sellerUsername,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sellerBookingService.getEditedTours(sellerUsername, page, size));
    }
    @GetMapping("/{bookingId}")
    public ResponseEntity<GeneralResponse<SellerBookingDetailDTO>> getBookingDetail(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(sellerBookingService.getBookingDetail(bookingId));
    }
}