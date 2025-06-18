package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import com.fpt.capstone.tourism.dto.response.UserManagementDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserManagementService {

    public GeneralResponse<List<UserManagementDTO>> getListUsers();
}
