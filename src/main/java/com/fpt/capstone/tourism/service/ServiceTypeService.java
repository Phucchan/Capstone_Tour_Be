package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.ServiceTypeDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeDeleteStatusDTO;

import java.util.List;

public interface ServiceTypeService {
    GeneralResponse<List<ServiceTypeDTO>> getServiceTypes();

    GeneralResponse<ServiceTypeDTO> createServiceType(ServiceTypeDTO dto);

    GeneralResponse<ServiceTypeDTO> updateServiceType(Long id, ServiceTypeDTO dto);

    GeneralResponse<String> deleteServiceType(Long id);

    GeneralResponse<ServiceTypeDTO> changeStatus(Long id, ChangeDeleteStatusDTO changeStatusDTO);
}