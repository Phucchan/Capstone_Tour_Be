package com.fpt.capstone.tourism.dto.request.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerBookingUpdateRequestDTO {
    private String fullName;
    private String address;
    private String email;
    private String phone;
    private LocalDateTime paymentDeadline;
}