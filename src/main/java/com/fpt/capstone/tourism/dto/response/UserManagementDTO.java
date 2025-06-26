package com.fpt.capstone.tourism.dto.response;

import com.fpt.capstone.tourism.model.enums.Gender;
import com.fpt.capstone.tourism.model.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserManagementDTO {
    private Long id;
    private String fullName;
    private String email;
    private Gender gender;
    private String phone;
    private List<String> roleNames;
    private boolean deleted;
}
