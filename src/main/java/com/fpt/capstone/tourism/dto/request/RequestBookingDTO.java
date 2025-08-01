package com.fpt.capstone.tourism.dto.request;

import com.fpt.capstone.tourism.model.enums.RoomCategory;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestBookingDTO {
    private Long userId;
    private Long departLocationId;
    private Double priceMin;
    private Double priceMax;
    private String location;
    private String locationDetail;
    private LocalDate startDate;
    private LocalDate endDate;
    private TourTransport transport;
    private int adults;
    private int children;
    private int infants;
    private int hotelRooms;
    private RoomCategory roomCategory;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}