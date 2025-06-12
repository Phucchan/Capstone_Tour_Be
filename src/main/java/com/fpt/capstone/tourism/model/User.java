package com.fpt.capstone.tourism.model;

import lombok.Data;

@Data
public class User {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String address;
    private String role; // e.g., "USER", "ADMIN"
    private boolean isActive; // Indicates if the user account is active
}
