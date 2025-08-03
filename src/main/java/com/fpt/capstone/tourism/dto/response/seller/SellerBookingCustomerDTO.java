package com.fpt.capstone.tourism.dto.response.seller;

import com.fpt.capstone.tourism.model.enums.Gender;
import com.fpt.capstone.tourism.model.enums.PaxType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerBookingCustomerDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private Date dateOfBirth;
    private Gender gender;
    private PaxType paxType;
    private String pickUpAddress;
    private boolean singleRoom;
    private String note;
    private String status;
}