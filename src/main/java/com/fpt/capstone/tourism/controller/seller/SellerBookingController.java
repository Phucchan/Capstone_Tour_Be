package com.fpt.capstone.tourism.controller.seller;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import com.fpt.capstone.tourism.dto.request.seller.SellerBookingUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingDetailDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingSummaryDTO;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.service.SellerBookingService;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.service.tourbooking.TourBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/bookings")
public class SellerBookingController {

    private final SellerBookingService sellerBookingService;
    private final TourBookingService tourBookingService;

    @GetMapping("/available")
    public ResponseEntity<GeneralResponse<PagingDTO<SellerBookingSummaryDTO>>> getAvailableBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(sellerBookingService.getAvailableBookings(page, size));
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
    //postman http://localhost:8080/v1/seller/bookings/17
    public ResponseEntity<GeneralResponse<SellerBookingDetailDTO>> getBookingDetail(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(sellerBookingService.getBookingDetail(bookingId));
    }

    //postman http://localhost:8080/seller/bookings/1/schedule?scheduleId=2
    // chỉnh sửa ngày khởi hành, từ đó lấy ra được schedule tương ứng
    @PutMapping("/{bookingId}/schedule")
    public ResponseEntity<GeneralResponse<SellerBookingDetailDTO>> updateBookingSchedule(
            @PathVariable Long bookingId,
            @RequestParam Long scheduleId) {
        return ResponseEntity.ok(sellerBookingService.updateBookingSchedule(bookingId, scheduleId));
    }

    @PatchMapping("/{bookingId}/claim")
    public ResponseEntity<GeneralResponse<SellerBookingDetailDTO>> claimBooking(
            @PathVariable Long bookingId,
            @RequestParam String sellerUsername) {
        return ResponseEntity.ok(sellerBookingService.claimBooking(bookingId, sellerUsername));
    }
    @PutMapping("/{bookingId}")
    // update thông tin khách hàng
    public ResponseEntity<GeneralResponse<SellerBookingDetailDTO>> updateBookedPerson(
            @PathVariable Long bookingId,
            @RequestBody SellerBookingUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(sellerBookingService.updateBookedPerson(bookingId, requestDTO));
    }

    @PostMapping("/{bookingId}/schedule/{scheduleId}/customers")
    public ResponseEntity<GeneralResponse<String>> addCustomersToSchedule(
            @PathVariable Long bookingId,
            @PathVariable Long scheduleId,
            @RequestBody List<BookingRequestCustomerDTO> customers) {
        tourBookingService.addCustomersToSchedule(bookingId, scheduleId, customers);
        return ResponseEntity.ok(GeneralResponse.of("success"));
    }
    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<GeneralResponse<SellerBookingDetailDTO>> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(sellerBookingService.updateBookingStatus(bookingId, status));
    }
}



