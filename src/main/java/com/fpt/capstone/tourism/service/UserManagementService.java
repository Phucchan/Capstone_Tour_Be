package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.UserManagementRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserFullInformationResponseDTO;
import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public interface UserManagementService {

    GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> getAllUsers(int page,
                                                                           int size,
                                                                           String keyword,
                                                                           Boolean isDeleted,
                                                                           String roleName,
                                                                           String sortField,
                                                                           String sortDirection);

    GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> getAllCustomers(int page,
                                                                               int size,
                                                                               String keyword,
                                                                               Boolean isDeleted,
                                                                               String sortField,
                                                                               String sortDirection);

    GeneralResponse<PagingDTO<UserFullInformationResponseDTO>> getAllStaff(int page,
                                                                           int size,
                                                                           String keyword,
                                                                           Boolean isDeleted,
                                                                           String sortField,
                                                                           String sortDirection);

    GeneralResponse<UserManagementDTO> createUser(UserManagementRequestDTO requestDTO);

    GeneralResponse<UserManagementDTO> updateUser(Long id, UserManagementRequestDTO requestDTO);

    GeneralResponse<String> deleteUser(Long id);

    GeneralResponse<UserManagementDTO> changeStatus(Long id, ChangeStatusDTO changeStatusDTO);

    Map<String, Boolean> checkUniqueness(String type, String value);
}

