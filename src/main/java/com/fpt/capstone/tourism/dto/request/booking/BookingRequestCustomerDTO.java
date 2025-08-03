package com.fpt.capstone.tourism.dto.request.booking;

import com.fpt.capstone.tourism.model.enums.Gender;
import com.fpt.capstone.tourism.model.enums.PaxType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestCustomerDTO {
    private String fullName;
    private Gender gender;
    private Date dateOfBirth;
    private boolean singleRoom;
    private PaxType paxType;
    private String email;
    private String phoneNumber;
    private String address;
    private String pickUpAddress;
    private String note;
}