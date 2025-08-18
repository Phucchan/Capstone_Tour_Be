package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.PendingServiceUpdateDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import java.util.List;

public interface PartnerServiceApprovalService {
    GeneralResponse<PagingDTO<ServiceInfoDTO>> getPendingServices(int page, int size, String keyword);
    GeneralResponse<ServiceInfoDTO> updateService(Long id, PendingServiceUpdateDTO dto);
}