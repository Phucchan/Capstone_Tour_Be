package com.fpt.capstone.tourism.dto.request.seller;

import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerBookingCreateRequestDTO {
    private Long userId;
    private Long tourId;
    private Long scheduleId;
    private String fullName;
    private String address;
    private String email;
    private String phone;
    private LocalDateTime paymentDeadline;
    private PaymentMethod paymentMethod;
    private String note;
    private List<BookingRequestCustomerDTO> customers;
}