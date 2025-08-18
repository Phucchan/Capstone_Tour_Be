package com.fpt.capstone.tourism.controller.booking;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestDTO;
import com.fpt.capstone.tourism.dto.response.booking.BookingConfirmResponse;
import com.fpt.capstone.tourism.service.RequestBookingService;
import com.fpt.capstone.tourism.service.VNPayService;
import com.fpt.capstone.tourism.service.tourbooking.TourBookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/booking")
public class BookingController {

    private final TourBookingService tourBookingService;
    private final VNPayService vnPayService;
    private final RequestBookingService requestBookingService;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @PostMapping("/submit")
    //postman http://localhost:8080/public/booking/submit
    public ResponseEntity<GeneralResponse<String>> submitBooking(@RequestBody BookingRequestDTO bookingRequestDTO){
        return ResponseEntity.ok(GeneralResponse.of(tourBookingService.createBooking(bookingRequestDTO)));
    }

    @GetMapping("/details/{bookingCode}")
    public ResponseEntity<GeneralResponse<BookingConfirmResponse>> getBookingDetails(@PathVariable("bookingCode") String bookingCode){
        return ResponseEntity.ok(GeneralResponse.of(tourBookingService.getTourBookingDetails(bookingCode)));
    }

    @GetMapping("/vnpay")
    public RedirectView getVnPayPayment(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);
        String orderInfo = request.getParameter("vnp_OrderInfo");


        String redirectUrl = String.format(
                frontendBaseUrl + "/tour-booking-detail/%s?status=%s",
                URLEncoder.encode(orderInfo, StandardCharsets.UTF_8),
                paymentStatus == 1 ? "success" : "fail"
        );

        tourBookingService.confirmPayment(paymentStatus, orderInfo);

        return new RedirectView(redirectUrl);
    }
    @PostMapping("/send-code")
    public ResponseEntity<GeneralResponse<String>> sendVerificationCode(@RequestParam("email") String email) {
        return ResponseEntity.ok(requestBookingService.sendVerificationCode(email));
    }

}
