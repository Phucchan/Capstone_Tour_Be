package com.fpt.capstone.tourism.dto.response;

import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistTourSummaryDTO {
    private Long wishlistId;
    private TourSummaryDTO tour;
}