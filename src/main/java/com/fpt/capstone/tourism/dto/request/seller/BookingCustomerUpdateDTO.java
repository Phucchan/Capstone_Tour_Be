package com.fpt.capstone.tourism.dto.request.seller;

import com.fpt.capstone.tourism.model.enums.Gender;
import com.fpt.capstone.tourism.model.enums.PaxType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCustomerUpdateDTO {
    private Long id;
    private String fullName;
    private String address;
    private String email;
    private Date dateOfBirth;
    private String phoneNumber;
    private String pickUpAddress;
    private Gender gender;
    private PaxType paxType;
    private boolean singleRoom;
}