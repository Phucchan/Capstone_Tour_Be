package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.ServiceTypeDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;

import java.util.List;

public interface ServiceTypeService {
    GeneralResponse<List<ServiceTypeDTO>> getServiceTypes();
}