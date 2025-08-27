package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ServiceCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.ServiceDetailDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.model.enums.PartnerServiceStatus;

public interface ServiceManagementService {
    GeneralResponse<PagingDTO<ServiceInfoDTO>> getServices(int page, int size, String keyword);
    GeneralResponse<ServiceInfoDTO> createService(ServiceCreateRequestDTO dto);
    GeneralResponse<ServiceDetailDTO> getServiceDetail(Long id);
    GeneralResponse<String> deleteService(Long id);
    GeneralResponse<ServiceInfoDTO> changeServiceStatus(Long id, PartnerServiceStatus status);
}
