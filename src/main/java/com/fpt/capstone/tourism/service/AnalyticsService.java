package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyNewUserDTO;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyRevenueDTO;
import com.fpt.capstone.tourism.dto.response.analytic.TourRevenueDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AnalyticsService {
    GeneralResponse<List<TourRevenueDTO>> getTopToursByRevenue(int limit);

    GeneralResponse<List<MonthlyRevenueDTO>> getMonthlyRevenue(Long tourId, int year);

    GeneralResponse<List<MonthlyNewUserDTO>> getMonthlyNewUsers(int year);
}