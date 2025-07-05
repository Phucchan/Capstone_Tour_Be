package com.fpt.capstone.tourism.controller.admin;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserFullInformationResponseDTO;
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
    // postman http://localhost:8080/v1/admin/users?page=1&size=6
    public ResponseEntity<GeneralResponse<PagingDTO<UserFullInformationResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) String roleName,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(userManagementService.getAllUsers(page, size, keyword, isDeleted, roleName, sortField, sortDirection));
    }
    @GetMapping("/users/customers")
    public ResponseEntity<GeneralResponse<PagingDTO<UserFullInformationResponseDTO>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(userManagementService.getAllCustomers(page, size, keyword, isDeleted, sortField, sortDirection));
    }

    @GetMapping("/users/staffs")
    public ResponseEntity<GeneralResponse<PagingDTO<UserFullInformationResponseDTO>>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        return ResponseEntity.ok(userManagementService.getAllStaff(page, size, keyword, isDeleted, sortField, sortDirection));
    }
    @PostMapping("/users")
    public ResponseEntity<GeneralResponse<UserManagementDTO>> createUser(@RequestBody UserManagementRequestDTO requestDTO) {
        return ResponseEntity.ok(userManagementService.createUser(requestDTO));
    }
    @PatchMapping("/users/{id}/status")
    // postman http://localhost:8080/v1/admin/users/1/status
    public ResponseEntity<GeneralResponse<UserManagementDTO>> changeStatus(@PathVariable Long id,
                                                                           @RequestBody ChangeStatusDTO changeStatusDTO) {
        return ResponseEntity.ok(userManagementService.changeStatus(id, changeStatusDTO));
    }

}
