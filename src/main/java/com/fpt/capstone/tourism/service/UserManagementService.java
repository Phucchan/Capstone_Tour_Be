package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface UserManagementService {

    public GeneralResponse<List<UserManagementDTO>> getListUsers();

    GeneralResponse<UserManagementDTO> createUser(UserManagementRequestDTO requestDTO);

    GeneralResponse<UserManagementDTO> updateUser(Long id, UserManagementRequestDTO requestDTO);

    GeneralResponse<String> deleteUser(Long id);
}
