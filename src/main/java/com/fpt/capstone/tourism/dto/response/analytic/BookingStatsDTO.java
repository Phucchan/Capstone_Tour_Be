package com.fpt.capstone.tourism.dto.response.analytic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatsDTO {
    private Long cancelledBookings;
    private Long returningCustomers;
}