package com.fpt.capstone.tourism.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementRequestDTO {
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private List<String> roleNames;
}