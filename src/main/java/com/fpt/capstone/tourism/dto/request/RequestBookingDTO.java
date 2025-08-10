package com.fpt.capstone.tourism.dto.request;

import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import com.fpt.capstone.tourism.model.enums.RoomCategory;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestBookingDTO {
    private Long id;

    private Long userId;

    private Long departureLocationId;

    private Double priceMin;
    private Double priceMax;

    private List<Long> destinationLocationIds;

    private String destinationDetail;

    private LocalDate startDate;

    private LocalDate endDate;

    private TourTransport transport;

    private String tourTheme;

    private String desiredServices;

    private LocalDate desiredDepartureDate;

    private Integer adults;

    private Integer children;

    private Integer infants;

    private Integer toddlers;

    private Integer hotelRooms;

    private RoomCategory roomCategory;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private RequestBookingStatus status;

    private String reason;
}