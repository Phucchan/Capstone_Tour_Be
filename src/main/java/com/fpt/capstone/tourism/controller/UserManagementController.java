package com.fpt.capstone.tourism.controller;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;


import com.fpt.capstone.tourism.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/")
public class UserManagementController {

    @Autowired
    private final UserManagementService userManagementService;

    @GetMapping("/users")
    public ResponseEntity<GeneralResponse<List<UserManagementDTO>>> getListUsers() {
        return ResponseEntity.ok(userManagementService.getListUsers());
    }
}
