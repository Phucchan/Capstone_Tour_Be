package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.PolicyDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.PolicyMapper;
import com.fpt.capstone.tourism.model.Policy;
import com.fpt.capstone.tourism.repository.PolicyRepository;
import com.fpt.capstone.tourism.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;

    @Override
    public GeneralResponse<List<PolicyDTO>> getPolicies() {
        List<PolicyDTO> dtos = policyRepository.findByDeletedFalse().stream()
                .map(policyMapper::policyToPolicyDTO)
                .toList();
        return new GeneralResponse<>(HttpStatus.OK.value(), "Get policy list successfully", dtos);
    }
}
