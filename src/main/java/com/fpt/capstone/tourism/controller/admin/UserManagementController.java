package com.fpt.capstone.tourism.controller.admin;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;


import com.fpt.capstone.tourism.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/users")
    public ResponseEntity<GeneralResponse<UserManagementDTO>> createUser(@RequestBody UserManagementRequestDTO requestDTO) {
        return ResponseEntity.ok(userManagementService.createUser(requestDTO));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<GeneralResponse<UserManagementDTO>> updateUser(@PathVariable Long id,
                                                                         @RequestBody UserManagementRequestDTO requestDTO) {
        return ResponseEntity.ok(userManagementService.updateUser(id, requestDTO));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<GeneralResponse<String>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.deleteUser(id));
    }
}
