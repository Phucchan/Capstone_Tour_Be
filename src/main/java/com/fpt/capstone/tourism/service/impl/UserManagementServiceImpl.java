package com.fpt.capstone.tourism.service.impl;


import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.repository.UserRepository;
import com.fpt.capstone.tourism.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public GeneralResponse<List<UserManagementDTO>> getListUsers() {
        List<User> users = userRepository.findAll();
        if(users != null && !users.isEmpty()) {
            List<UserManagementDTO> userManagementDTOs = users.stream()
                    .map(user -> new UserManagementDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getFullName(),
                            user.getGender(),
                            user.getPhone(),
                            user.getAddress(),
                            user.getAvatarImage(),
                            user.getUserRoles().stream()
                                    .map(userRole -> userRole.getRole().getRoleName())
                                    .toList(),
                            user.getDeleted(),
                            user.getCreatedAt(),
                            user.getUpdatedAt()

                    )).toList();
            return GeneralResponse.<List<UserManagementDTO>>builder()
                    .data(userManagementDTOs)
                    .message("User list retrieved successfully")
                    .build();

        } else{
            return GeneralResponse.<List<UserManagementDTO>>builder()
                    .data(List.of())
                    .message("No users found")
                    .build();
        }

    }


}
