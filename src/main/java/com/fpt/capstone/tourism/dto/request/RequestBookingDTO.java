package com.fpt.capstone.tourism.dto.request;

import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import com.fpt.capstone.tourism.model.enums.RoomCategory;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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
    private List<Long> tourThemeIds;
    private String desiredServices;
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
    private String verificationCode;
    private String reason;
}