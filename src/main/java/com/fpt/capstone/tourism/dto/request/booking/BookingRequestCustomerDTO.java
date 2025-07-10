package com.fpt.capstone.tourism.dto.request.booking;

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
public class BookingRequestCustomerDTO {
    private String fullName;
    private Gender gender;
    private Date dateOfBirth;
    private boolean singleRoom;
    private PaxType paxType;
}