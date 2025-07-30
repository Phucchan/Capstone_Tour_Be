package com.fpt.capstone.tourism.dto.request.booking;

import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingBasicRequestDTO {
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
}