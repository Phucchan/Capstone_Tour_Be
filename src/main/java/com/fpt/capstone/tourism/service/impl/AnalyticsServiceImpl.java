package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.analytic.*;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyRevenueDTO;
import com.fpt.capstone.tourism.dto.response.analytic.TourRevenueDTO;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public GeneralResponse<List<TourRevenueDTO>> getTopToursByRevenue(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = bookingRepository.findTopToursByRevenue(pageable);
        List<TourRevenueDTO> dtos = results.stream()
                .map(r -> TourRevenueDTO.builder()
                        .id(((Number) r[0]).longValue())
                        .name((String) r[1])
                        .tourType(r[2] != null ? r[2].toString() : null)
                        .totalRevenue(((Number) r[3]).doubleValue())
                        .build())
                .collect(Collectors.toList());
        return GeneralResponse.of(dtos);
    }

    @Override
    public GeneralResponse<List<MonthlyRevenueDTO>> getMonthlyRevenue(Long tourId, int year) {
        List<Object[]> results = bookingRepository.findMonthlyRevenueByTour(tourId, year);
        List<MonthlyRevenueDTO> dtos = results.stream()
                .map(r -> MonthlyRevenueDTO.builder()
                        .year(((Number) r[0]).intValue())
                        .month(((Number) r[1]).intValue())
                        .revenue(((Number) r[2]).doubleValue())
                        .build())
                .collect(Collectors.toList());
        return GeneralResponse.of(dtos);
    }
    @Override
    public GeneralResponse<List<MonthlyNewUserDTO>> getMonthlyNewUsers(int year) {
        List<Object[]> results = userRepository.countNewUsersByMonth(year);
        List<MonthlyNewUserDTO> dtos = results.stream()
                .map(r -> MonthlyNewUserDTO.builder()
                        .year(((Number) r[0]).intValue())
                        .month(((Number) r[1]).intValue())
                        .userCount(((Number) r[2]).longValue())
                        .build())
                .collect(Collectors.toList());
        return GeneralResponse.of(dtos);
    }
}
