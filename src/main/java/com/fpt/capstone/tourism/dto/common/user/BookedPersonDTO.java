package com.fpt.capstone.tourism.dto.common.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedPersonDTO {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
}