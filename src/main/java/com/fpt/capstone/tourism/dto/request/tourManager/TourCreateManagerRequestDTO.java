package com.fpt.capstone.tourism.dto.request.tourManager;

import com.fpt.capstone.tourism.model.enums.TourTransport;
import com.fpt.capstone.tourism.model.enums.TourType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourCreateManagerRequestDTO {
    private Long requestBookingId;
    private String name;
    private List<Long> tourThemeIds;
    private Long departLocationId;
    private List<Long> destinationLocationIds;
    private Integer durationDays;
    private String description;
    private TourType tourType;
    private TourTransport tourTransport;
}