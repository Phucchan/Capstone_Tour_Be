package com.fpt.capstone.tourism.dto.response.booking;

import com.fpt.capstone.tourism.dto.common.tour.TourScheduleShortInfoDTO;
import com.fpt.capstone.tourism.dto.common.tour.TourShortInfoDTO;
import com.fpt.capstone.tourism.dto.common.user.BookedPersonDTO;
import com.fpt.capstone.tourism.dto.common.user.TourCustomerDTO;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
public class BookingConfirmResponse {
    private Long id;
    private String bookingCode;
    private LocalDateTime createdAt;
    private BookedPersonDTO bookedPerson;
    private Double sellingPrice;
    private Double totalAmount;
    private Double extraHotelCost;
    private PaymentMethod paymentMethod;
    private TourShortInfoDTO tour;
    private String note;
    private TourScheduleShortInfoDTO tourSchedule;
    private List<TourCustomerDTO> adults;
    private List<TourCustomerDTO> children;
    private List<TourCustomerDTO> infants;
    private List<TourCustomerDTO> toddlers;
    private String paymentUrl;
    private BookingStatus status;
    private boolean needHelp;
    private int singleRooms;
}