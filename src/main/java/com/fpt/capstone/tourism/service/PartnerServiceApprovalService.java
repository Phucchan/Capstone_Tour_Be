package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import java.util.List;

public interface PartnerServiceApprovalService {
    GeneralResponse<List<ServiceInfoDTO>> getPendingServices();
    GeneralResponse<ServiceInfoDTO> changeStatus(Long id, ChangeStatusDTO dto);
}