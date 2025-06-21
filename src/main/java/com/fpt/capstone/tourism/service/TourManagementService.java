package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.response.TourResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TourManagementService {

    public GeneralResponse<List<TourResponseDTO>> getListTours();

    public GeneralResponse<Object> changeStatus(Long id, ChangeStatusDTO changeStatusDTO);
}
