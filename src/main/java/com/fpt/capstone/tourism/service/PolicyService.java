package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.PolicyDTO;

import java.util.List;

public interface PolicyService {
    GeneralResponse<List<PolicyDTO>> getPolicies();
}