package com.fpt.capstone.tourism.controller.business;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.analytic.BookingStatsDTO;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyNewUserDTO;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyRevenueDTO;
import com.fpt.capstone.tourism.dto.response.analytic.TourRevenueDTO;
import com.fpt.capstone.tourism.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business/revenue")
public class DashboardController {

    private final AnalyticsService analyticsService;

    @GetMapping("/top-tours")
    public ResponseEntity<GeneralResponse<List<TourRevenueDTO>>> getTopTours(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getTopToursByRevenue(limit, startDate, endDate));
    }

    @GetMapping({"/tours/{tourId}/monthly", "/monthly"})
    public ResponseEntity<GeneralResponse<List<MonthlyRevenueDTO>>> getMonthlyRevenue(@PathVariable(required = false) Long tourId,
                                                                                      @RequestParam int year,
                                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getMonthlyRevenue(tourId, year, startDate, endDate));
    }


    // postman http://localhost:8080/admin/revenue/tours/1/monthly?year=2023
    @GetMapping("/users/monthly")
    public ResponseEntity<GeneralResponse<List<MonthlyNewUserDTO>>> getMonthlyNewUsers(
            @RequestParam int year,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
         return ResponseEntity.ok(analyticsService.getMonthlyNewUsers(year, startDate, endDate));
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<GeneralResponse<Double>> getTotalRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getTotalRevenue(startDate, endDate));
    }

    @GetMapping("/total-bookings")
    public ResponseEntity<GeneralResponse<Long>> getTotalBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getTotalBookings(startDate, endDate));
    }
    @GetMapping("/total-new-users")
    //postman http://localhost:8080/business/revenue/total-new-users?startDate=2023-01-01&endDate=2023-12-31
    public ResponseEntity<GeneralResponse<Long>> getTotalNewUsers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getTotalNewUsers(startDate, endDate));
    }
    @GetMapping("/bookings/stats")
    public ResponseEntity<GeneralResponse<BookingStatsDTO>> getBookingStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getBookingStats(startDate, endDate));
    }
}
