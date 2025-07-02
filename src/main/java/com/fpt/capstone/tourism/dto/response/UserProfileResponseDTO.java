package com.fpt.capstone.tourism.dto.response;

import com.fpt.capstone.tourism.model.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserProfileResponseDTO {

    private String email;
    private String fullName;
    private Gender gender;
    private Integer totalToursBooked;
    private String phone;
    private String address;
    private String avatarImg;
    private LocalDate dateOfBirth;
    private Integer points;
}
