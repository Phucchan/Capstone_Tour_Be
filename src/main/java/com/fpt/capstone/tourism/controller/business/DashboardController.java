package com.fpt.capstone.tourism.controller.business;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyNewUserDTO;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyRevenueDTO;
import com.fpt.capstone.tourism.dto.response.analytic.TourRevenueDTO;
import com.fpt.capstone.tourism.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/revenue")
public class DashboardController {

    private final AnalyticsService analyticsService;

    @GetMapping("/top-tours")
    public ResponseEntity<GeneralResponse<List<TourRevenueDTO>>> getTopTours(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(analyticsService.getTopToursByRevenue(limit));
    }

    @GetMapping("/tours/{tourId}/monthly")
    public ResponseEntity<GeneralResponse<List<MonthlyRevenueDTO>>> getMonthlyRevenue(@PathVariable Long tourId,
                                                                                      @RequestParam int year) {
        return ResponseEntity.ok(analyticsService.getMonthlyRevenue(tourId, year));
    }
    // postman http://localhost:8080/admin/revenue/tours/1/monthly?year=2023
    @GetMapping("/users/monthly")
    public ResponseEntity<GeneralResponse<List<MonthlyNewUserDTO>>> getMonthlyNewUsers(@RequestParam int year) {
        return ResponseEntity.ok(analyticsService.getMonthlyNewUsers(year));
    }
}