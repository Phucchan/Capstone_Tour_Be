package com.fpt.capstone.tourism.dto.request;

import com.fpt.capstone.tourism.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDTO {
    private String fullName;
    private String email;
    private Gender gender;
    private String phone;
    private String address;
    private String avatarImg;
    private LocalDate dateOfBirth;
}