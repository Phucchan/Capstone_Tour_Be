package com.fpt.capstone.tourism.dto.request.booking;

import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private Long userId;
    private Long tourId;
    private Long scheduleId;
    private String fullName;
    private String note;
    private String phone;
    private String address;
    private PaymentMethod paymentMethod;
    private String email;
    private String verificationCode;
    private List<BookingRequestCustomerDTO> adults;
    private List<BookingRequestCustomerDTO> children;
    private List<BookingRequestCustomerDTO> infants;
    private List<BookingRequestCustomerDTO> toddlers;
    private int numberSingleRooms;
    private boolean needHelp;
    private Double total;
    private Double sellingPrice;
    private Double extraHotelCost;
    private String tourName;
    private Long userVoucherId;
}
