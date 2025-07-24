package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyNewUserDTO;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyRevenueDTO;
import com.fpt.capstone.tourism.dto.response.analytic.TourRevenueDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface AnalyticsService {
    GeneralResponse<List<TourRevenueDTO>> getTopToursByRevenue(int limit,
                                                               LocalDate startDate,
                                                               LocalDate endDate);

    GeneralResponse<List<MonthlyRevenueDTO>> getMonthlyRevenue(Long tourId,
                                                               int year,
                                                               LocalDate startDate,
                                                               LocalDate endDate);

    GeneralResponse<List<MonthlyNewUserDTO>> getMonthlyNewUsers(int year,
                                                                LocalDate startDate,
                                                                LocalDate endDate);

    GeneralResponse<Double> getTotalRevenue(LocalDate startDate,
                                            LocalDate endDate);

    GeneralResponse<Long> getTotalBookings(LocalDate startDate,
                                           LocalDate endDate);

    GeneralResponse<Long> getTotalNewUsers(LocalDate startDate,
                                           LocalDate endDate);
}